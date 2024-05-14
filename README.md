## Описание
В классе ApiTests соддержаться автотесты для метода POST из сервиса https://jsonplaceholder.typicode.com
findTop10FrequentWords метод ищет топ 10 слов в body ответа от сервиса 

Проект написан на Kotlin версии: 1.9
JDK: 17


## Запуск проекта 
1. Выполнить кнон проекта 
2. Выполнить:                   ./gradlew test    
3. Сгенерировать Allure отчет:  ./gradlew allureReport --clean     
4. Загрузить Allure отчет:      ./gradlew allureServe 