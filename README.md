# Запуск програми

Цей проект підтримує два профілі: **local** та **prod**. Ось як можна запустити програму для кожного з профілів.

## Локальний профіль (`local`)

Для запуску програми в локальному середовищі (наприклад, для тестування або розробки), використовуйте наступну команду:

mvn spring-boot:run

Це запустить програму з налаштуваннями для локального середовища.

## Продукційний профіль (`prod`)

Для запуску програми в продукційному середовищі, де налаштовано підключення до реальної бази даних і інші конфігурації, використовуйте наступну команду:

$env:SPRING_PROFILES_ACTIVE="prod"; mvn spring-boot:run
