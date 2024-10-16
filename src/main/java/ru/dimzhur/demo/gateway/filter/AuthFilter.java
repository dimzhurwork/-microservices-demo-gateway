package ru.dimzhur.demo.gateway.filter;

import jakarta.ws.rs.NotAuthorizedException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Фильтриавторизации
 */
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {


    /**
     * Конфигурация фильтра
     */
    static class Config {
    }

    /**
     * Секретная фраза для подписи
     */
    @Value("security.sold")
    private String sold;

    /**
     * Шаблон для обращения к RabbitMQ
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var headers = new HttpHeaders();

            var builder = exchange.getRequest().mutate();

            var authHeaders = exchange.getRequest().getHeaders().get("Authorization");
            String authHeader = null;
            if (authHeaders != null && !authHeaders.isEmpty()) {
                authHeader = authHeaders.get(0);
            }
            var authParametr = exchange.getRequest().getQueryParams().getFirst("token");

            if (authHeader == null) {
                authHeader = authParametr;
            } else {
                if (authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring("Bearer ".length());
                }
            }

            if (authHeader != null && !authHeader.isEmpty()) {
                System.out.println("Token=" + authHeader);
                var user = getUserRemote(authHeader);

                if(user != null){
                    headers.add("user-id", user.getId());
                    for(String role : user.getRoles()){
                        headers.add("user-role", role);
                    }
                    headers.add("sign", createHash(user));
                }else{
                    throw new NotAuthorizedException("invalid token");
                }

            } else {
                System.out.println("Token is empty");
            }


            builder.headers( httpHeaders -> {
                httpHeaders.remove("authorization");
                httpHeaders.remove("user-role");
                httpHeaders.remove("user-id");
                for(String key : headers.keySet()){
                    var values = headers.get(key);
                    if(values != null){
                       for(String value : values){
                           httpHeaders.add(key, value);
                       }
                    }
                }
            });
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }

    /**
     * Получение пользователя из сервиса пользователей
     *
     * @param token токен пользователя
     * @return данные пользователя
     */
    private UserData getUserRemote(String token) {
        return (UserData) rabbitTemplate.convertSendAndReceive("auth.rpc.validate", token);
    }

    /**
     * Создание подписи данных пользователя
     * @param user пользователь
     * @return подпись
     */
    private String createHash(UserData user) {
        String text = "";
        text += user.getId();
        List<String> roles = user.getRoles();
        roles.sort(String::compareTo);
        for(String role : roles){
            text += role;
        }
        text += sold;
        return DigestUtils.sha256Hex(text);
    }
}
