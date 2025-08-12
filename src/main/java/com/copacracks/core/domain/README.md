# Modelo de Usuário - Princípios de Modelo Rico

Este diretório contém o modelo de usuário implementado seguindo os princípios de **Rich Domain Model** e **Domain-Driven Design (DDD)**.

## Estrutura

### Classe Principal: `User`
- **Entidade principal** que representa um usuário no sistema
- Encapsula regras de negócio e validações
- Imutável para o ID, mas permite alterações controladas de outros atributos

### Value Objects (Records)
- **`Email`**: Representa um email válido com validação de formato
- **`Password`**: Representa uma senha segura com criptografia SHA-256 + salt
- **`Username`**: Representa um nome de usuário válido com validações

### Exceções
- **`UserValidationException`**: Exceção personalizada para erros de validação

## Princípios Aplicados

### 1. Encapsulamento
- Atributos privados com acesso controlado
- Validações encapsuladas nos construtores e métodos
- Comportamentos relacionados aos dados agrupados na mesma classe

### 2. Validação de Invariantes
- Username: 3-50 caracteres, apenas letras, números e underscore
- Password: mínimo 8 caracteres, com maiúscula, minúscula, número e caractere especial
- Email: formato válido de email

### 3. Imutabilidade
- ID é final e não pode ser alterado
- Value Objects são imutáveis
- Alterações são feitas através de métodos específicos

### 4. Comportamentos Ricos
- `changePassword()`: altera senha com validação
- `changeEmail()`: altera email com validação
- `changeUsername()`: altera username com validação
- `isPasswordValid()`: verifica se uma senha corresponde
- `isNew()`: verifica se o usuário é novo

### 5. Value Objects (Records)
- **Email**: encapsula validação e formatação de email
- **Password**: encapsula criptografia e validação de senha
- **Username**: encapsula validação de nome de usuário
- Imutáveis, comparáveis por valor e com menos boilerplate

## Uso

### Criação de Usuário
```java
// Usuário novo
User user = new User("john_doe", "SecurePass123!", "john@example.com");

// Usuário existente
User existingUser = new User(1L, "john_doe", "SecurePass123!", "john@example.com");
```

### Alterações
```java
user.changePassword("NewSecurePass456!");
user.changeEmail("john.doe@company.com");
user.changeUsername("jane_doe");
```

### Validação
```java
if (user.isPasswordValid("SecurePass123!")) {
    // Senha correta
}
```

### Uso dos Records (Value Objects)
```java
// Criar Value Objects diretamente
Username username = Username.of("john_doe");
Email email = Email.of("john@example.com");
Password password = Password.of("SecurePass123!");

// Os records são imutáveis e validam automaticamente
// Username.of("ab"); // Throws UserValidationException
// Email.of("invalid"); // Throws UserValidationException
// Password.of("weak"); // Throws UserValidationException
```

## Vantagens

1. **Segurança**: Senhas são criptografadas com salt único
2. **Validação**: Regras de negócio são aplicadas automaticamente
3. **Manutenibilidade**: Lógica centralizada e fácil de modificar
4. **Testabilidade**: Comportamentos isolados e testáveis
5. **Consistência**: Invariantes sempre respeitadas
6. **Modernidade**: Uso de records para Value Objects (Java 14+)
7. **Menos Boilerplate**: equals, hashCode e toString gerados automaticamente

## Testes

Execute os testes para verificar o funcionamento:
```bash
./gradlew test
```

Os testes cobrem:
- Criação de usuários válidos
- Validação de senha, email e username
- Alterações de atributos
- Comportamentos do modelo
