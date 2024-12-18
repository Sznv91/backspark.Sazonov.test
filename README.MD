# Тестовое задание

## Создать REST API для учета носков на складе магазина.

### Функционал
#### Регистрация прихода носков:

- POST /api/socks/income
Параметры: цвет носков, процентное содержание хлопка, количество.
Увеличивает количество носков на складе.
Регистрация отпуска носков:

- POST /api/socks/outcome
Параметры: цвет носков, процентное содержание хлопка, количество.
Уменьшает количество носков на складе, если их хватает.


#### Получение общего количества носков с фильтрацией:

- GET /api/socks
  - Фильтры:
    - Цвет носков.
    - Оператор сравнения (moreThan, lessThan, equal).
    - Процент содержания хлопка.

  Возвращает количество носков, соответствующих критериям.


#### Обновление данных носков:

- PUT /api/socks/{id}.
Позволяет изменить параметры носков (цвет, процент хлопка, количество).


#### Загрузка партий носков из Excel или CSV (один формат на выбор) файла:

- POST /api/socks/batch
Принимает Excel или CSV (один формат на выбор) файл с партиями носков, содержащими цвет, процентное содержание хлопка и количество.



### Дополнительные требования
#### Логирование:

- Реализовать логирование всех операций (приход, отпуск, обновление, запросы) с использованием SLF4J.

#### Дополнительные критерии поиска:
- Возможность фильтрации по диапазону процентного содержания хлопка (например, от 30 до 70%).
Возможность сортировки результата по цвету или проценту хлопка.


#### Улучшенная обработка ошибок:

- Реализовать централизованную обработку ошибок с использованием @ControllerAdvice.
- Возвращать понятные сообщения об ошибках:
  - Некорректный формат данных.
  - Нехватка носков на складе.
  - Ошибки при обработке файлов.


#### Документация:

- Использовать Swagger/OpenAPI для автоматической генерации документации.



### Требования
- Язык реализации: Java 17.
- Фреймворк: Spring Boot 2.7.
- База данных:  PostgreSQL.
- версия JDK : 17
- Система сборки: Gradle
- Формат файла для загрузки партий: Excel (.xlsx) или .csv  (один формат на выбор)
- Тестирование: Покрытие тестами минимум 50% кода.

---
# Выполненное задание
Задание выполнено в полном объеме с учётом дополнительных требований. Дополнительно к требованию по логированию
сделал вывод в консоль логов всех фрейморков, а в файл только логи контроллера и сервисов приложения тестового задания.
При выполнении задания не совсем были понятны требования по фильтрации, поэтому было принято не однозначное решение
о разных форматах возвращаемых ответов. В случае использования одновременно всех фильтров
- Цвет носков.
- Оператор сравнения (~~moreThan, lessThan~~, **equal**).
- Процент содержания хлопка.

возвращается только число соответствующее запросам. При любых других комбинациях фильтров или операторов сравнения, будет
возвращён JSON с массивом данных, содержащих в себе информацию о цветах, содержании хлопка, и остатках на складе по всем
подходящим к критериям поиска объектов.

## Инструкция по запуску.
Приложение имеет в своих зависимостях контейнер сервлетов Tomcat. Поэтому оно запускается прямо из консоли, без необходимости
дополнительного деплоя в уже существующий контейнер сервлетов.

Для сборки проекта необходима версия Java == 17.
Сборка и запуск состоит из нескольких шагов:
- Загрузить репозиторий с github:<br>
  `git clone https://github.com/Sznv91/backspark.Sazonov.test.git` <br>
- Перейти в каталог загруженного репозитория <br>
  `cd .\test.sazonov` <br>
- Выполнить смену крешеншенелов для доступа к БД в файле: <br>
  `.\src\main\resources\application.properties` <br>
- Выполнить сборку проекта средствами Gradle, во время сборки так же будет выполнено тестирование классов сервиса и rest контроллера<br>
  `gradlew build` <br>
- Запустить приложение: <br>
  `java -jar .\build\libs\test.sazonov-0.0.1-SNAPSHOT.war`
- После успешного запуска, можно ознакомиться с автоматически сгенерированной документацией Swagger:<br>
`http://localhost:8080/swagger-ui/index.html`

Перед запуском приложения необязательно в ручную запускать скрипт создания таблицы БД, он запускается автоматически при каждом старте приложения.<br>
Файл с логами будет находиться в том же каталоге из которого выполнен запуск приложения.