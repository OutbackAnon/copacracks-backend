#!/bin/sh
echo "Executando verificação antes do commit..."
./gradlew preCommitCheck --quiet
RESULT=$?

if [ $RESULT -ne 0 ]; then
  echo "❌ Commit cancelado: erros encontrados."
  exit 1
fi

echo "✅ Verificações concluídas."
exit 0
