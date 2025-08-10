# copacracks-backend

Muda a versão do gradle
``
./gradlew wrapper --gradle-version <VERSION>
``

Clean build
``
./gradlew clean build
``

Roda o checkstyle
``./gradlew checkstyleMain
``

Outra comando para rodar o checkstyle
`` 
./gradlew checkstyleTest 
``

Verifica a formatação dos arquivos
``./gradlew spotlessCheck
``

Aplica a formatação automatica
``./gradlew spotlessApply
``

Roda o test coverage
``./gradlew test jacocoTestReport
``

Roda o PMD
``./gradlew pmdMain
``

Roda o SpotBugs
``./gradlew spotbugsMain
``

Roda o SpotBugs e o PMD
``./gradlew codeQuality
``

