# FatecADS — execução local

Aplicação Spring Boot com Thymeleaf e PostgreSQL. Para iniciar, basta Docker com Compose v2 e acesso à internet na primeira construção. Java e Maven são usados apenas dentro do build Docker; não precisam estar instalados na máquina.

## Iniciar

Execute os comandos dentro de `fatecads/`:

```bash
cd fatecads
```

Na primeira execução, crie `.env` a partir de `.env.example` e preencha `DB_PASSWORD` com uma senha local. Alternativamente, com OpenSSL instalado, gere o arquivo com uma senha aleatória:

```bash
# Somente na primeira execução: não sobrescreva um .env existente.
(umask 077; printf 'DB_PASSWORD=%s\nSERVER_PORT=8081\n' "$(openssl rand -hex 24)" > .env)
```

O `.env` não é versionado nem enviado no contexto de build da imagem. O Compose lê esse arquivo para configurar os containers. Neste ambiente ele já foi criado: preserve-o para continuar acessando o banco existente.

Para construir a imagem e iniciar aplicação e banco:

```bash
docker compose up -d --build --wait
```

A aplicação só inicia depois que o PostgreSQL estiver saudável. O comando espera também pelo healthcheck HTTP da aplicação. A imagem usa build multiestágio, Java 17 no runtime, usuário sem privilégios e cache das dependências Maven.

Nas próximas vezes, sem alterações no código:

```bash
docker compose up -d --wait
```

Depois de alterar código ou dependências, use novamente `docker compose up -d --build --wait`.

Abra <http://localhost:8081/>. Use **Criar uma Conta** para cadastrar o primeiro acesso. O fluxo existente permite selecionar Administrador (área acadêmica) ou Cliente (loja); o login direciona para a área correspondente. Não há conta nem senha de acesso predefinidas.

## Configuração

- `SERVER_PORT`: porta HTTP publicada no host; padrão `8081`. Dentro do container, a aplicação sempre usa `8081`.
- `DB_PORT`: porta do PostgreSQL publicada no host; padrão `5432`. A aplicação usa a rede interna do Compose, em `db:5432`.
- `DB_USERNAME`: usuário do banco; padrão `postgres`.
- `DB_PASSWORD`: senha obrigatória do banco.

Aplicação e banco publicam portas apenas em `127.0.0.1`, para uso local. Os dados continuam no volume `fatecads_postgres_data`, inclusive após remover e recriar os containers. Alterar usuário ou senha no `.env` não altera as credenciais de um banco já inicializado: preserve o arquivo ou altere as credenciais no PostgreSQL também.

Os dois serviços usam `restart: unless-stopped`: voltam com o Docker se não tiverem sido parados explicitamente. A imagem não contém o `.env`, o código-fonte nem as ferramentas de build.

## Consultar, parar e reiniciar

Execute dentro de `fatecads/`:

```bash
# Estado e healthchecks
docker compose ps

# Acompanhar os logs da aplicação; Ctrl+C sai apenas dos logs
docker compose logs -f app

# Parar tudo, mantendo containers e dados
docker compose stop

# Iniciar novamente
docker compose up -d --wait

# Remover containers e rede, preservando o volume de dados
docker compose down
```

Não use `docker compose down -v` para uma parada normal: a opção `-v` apaga o volume do banco.

## Testes de desenvolvimento

O build da imagem compila e empacota com `-DskipTests`: não executa os testes, pois eles dependem de um banco em execução. Para rodar a suíte existente, é necessário Java 17 ou superior no host:

```bash
docker compose up -d --wait db
./mvnw test
```

Os testes usam a conexão local configurada pelo `.env` e podem atualizar o esquema; execute apenas contra um banco de desenvolvimento, nunca produção. Para execução Java fora do Docker, `DB_URL` pode substituir a URL JDBC; o Compose define sua própria URL para o banco interno.

## Limites do ambiente local

Esta configuração não é um deploy de produção. O cadastro público existente permite selecionar Administrador e a configuração de segurança desabilita CSRF; não exponha o sistema à internet sem rever esses controles.

O serviço opcional `fatecads/whatsapp-service/` não está incluído neste Compose. A recuperação de senha por WhatsApp não está disponível nesta configuração: o código atual aponta para `localhost:3000`, que dentro do container é o próprio container da aplicação. Esse fluxo exige integração com o serviço separado e uma sessão válida do WhatsApp; não é necessário para cadastrar contas ou fazer login.
