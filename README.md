# Gateway

### Описание

Сервис для маршрутизации запросов на другие сервисы.
Помимо маршрутизации в запросы добавляет заголовки с данными пользователя полученными из сервиса авторизации

# Заголовки

* user-id - ID пользователя в системе
* user-role - Роль пользователя, может быть несколько
* sign - подпись данных пользователя - Sha256 из id + roles + sold, где id - ID пользователя, roles - список всех ролей пользователя в алфавитном порядке без пробелов, sold - дополнительная секретная фраза


## License

The MIT License (MIT)

Copyright (c) 2024 Dmitriy Zhuravlev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
