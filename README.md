# FatecADS — execução local

Aplicação Spring Boot com Thymeleaf e PostgreSQL. Para iniciar, basta Docker com Compose v2 e acesso à internet na primeira construção. Java e Maven são usados apenas dentro do build Docker; não precisam estar instalados na máquina.

## Requisitos

- Docker instalado e em execução, com permissão para executar containers.
- Docker Compose v2 com suporte a `--wait`.
- Internet na primeira construção para baixar imagens e dependências.
- Git, caso vá clonar o repositório; OpenSSL é opcional para gerar a senha local.

Confira as ferramentas:

```bash
docker --version
docker compose version
```

Para baixar o projeto:

```bash
git clone https://github.com/Carlos-Candido/Entrega-Continua-DevOps.git
cd Entrega-Continua-DevOps
```

## Iniciar

Execute os comandos dentro de `fatecads/`:

```bash
cd fatecads
```

Na primeira execução, copie `.env.example` para `.env` e preencha `DB_PASSWORD` com uma senha local. Não sobrescreva um `.env` existente: ele contém a configuração usada pelo banco.

Como alternativa, em Linux/macOS ou Git Bash com OpenSSL instalado, este comando cria o arquivo somente se ele ainda não existir:

```bash
if [ ! -f .env ]; then
  (umask 077; printf 'DB_PASSWORD=%s\nSERVER_PORT=8081\n' "$(openssl rand -hex 24)" > .env)
fi
```

O `.env` não é versionado nem enviado no contexto de build da imagem. O Compose lê esse arquivo para configurar os containers. Guarde-o para continuar acessando o banco existente.

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

## Login e perfis

O formulário de login é o mesmo para todos os perfis. Entre com o **login escolhido no cadastro**, não com o email.

| Perfil escolhido no cadastro | Destino após login | Área disponível |
| --- | --- | --- |
| Administrador | `/home` | Gestão de alunos, cursos, professores, disciplinas, usuários, produtos e pedidos |
| Cliente (loja) | `/loja` | Vitrine, catálogo de produtos, carrinho e finalização de compra |

Cliente não pode acessar as rotas administrativas. Administrador não acessa a loja com a mesma conta: crie uma conta Cliente separada para experimentar os dois fluxos. Para trocar de conta, use **Sair** e faça login novamente.

## Como usar a loja / marketplace

O banco novo não recebe produtos de demonstração automaticamente. Para preencher a vitrine:

1. Crie uma conta com perfil **Administrador** e faça login.
2. Abra **Produtos** → formulário de novo produto, ou acesse `/produtos/criar`.
3. Informe descrição, valor, unidade e marca. A imagem é opcional.
4. Salve o produto e saia da conta administrativa.
5. Cadastre uma segunda conta com perfil **Cliente (loja)** e faça login.
6. No catálogo, pesquise ou filtre por marca, abra um produto e adicione ao carrinho.
7. No carrinho, altere quantidades ou remova itens; finalize para registrar o pedido e ver a confirmação.

Cada pedido finalizado fica vinculado ao cliente autenticado. O carrinho fica na sessão do navegador, não é um carrinho persistente entre sessões. A compra é simulada: não há cobrança nem integração com meios de pagamento.


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

## Executar a aplicação sem Docker

Alternativa para desenvolvimento: exige Java 17 ou superior no host. O PostgreSQL pode continuar no Docker. Use o mesmo `.env` e execute dentro de `fatecads/`:

```bash
# Libera a porta HTTP ocupada pela aplicação no container
docker compose stop app
docker compose up -d --wait db
./mvnw spring-boot:run
```

No Windows, use `.\mvnw.cmd spring-boot:run`. O wrapper baixa o Maven automaticamente. Abra a porta definida em `SERVER_PORT`; o padrão continua sendo `8081`. Use `Ctrl+C` para encerrar a execução Java.

Para voltar ao modo totalmente Docker, encerre primeiro o Java e execute `docker compose up -d --wait`.


## Testes de desenvolvimento

O build da imagem compila e empacota com `-DskipTests`: não executa os testes, pois eles dependem de um banco em execução. Para rodar a suíte existente, é necessário Java 17 ou superior no host:

```bash
docker compose up -d --wait db
./mvnw test
```

Os testes usam a conexão local configurada pelo `.env` e podem atualizar o esquema; execute apenas contra um banco de desenvolvimento, nunca produção. Para execução Java fora do Docker, `DB_URL` pode substituir a URL JDBC; o Compose define sua própria URL para o banco interno.

## Problemas comuns

| Sintoma | O que conferir |
| --- | --- |
| `Defina DB_PASSWORD no arquivo .env` | Crie `fatecads/.env` e preencha `DB_PASSWORD`; `.env.example` sozinho não é carregado. |
| Porta já em uso | Altere `SERVER_PORT` ou `DB_PORT` no `.env` e execute `docker compose up -d --wait`. Se trocar `SERVER_PORT`, use essa porta no navegador. |
| Falha ao conectar ao Docker | Inicie o Docker e confira as permissões do seu usuário. |
| Erro de autenticação no PostgreSQL após mudar a senha | Restaure a configuração original ou altere a senha também no PostgreSQL. O volume mantém as credenciais anteriores. |
| Loja sem produtos | Cadastre produtos com uma conta Administrador; não há carga automática de catálogo. |
| Acesso negado ao abrir loja ou administração | Confira o perfil da conta; cada área exige o perfil correspondente. |

Se o início falhar, consulte o estado e os logs:

```bash
docker compose ps
docker compose logs --tail=100 app db
```


## Limites do ambiente local

Esta configuração não é um deploy de produção. O cadastro público existente permite selecionar Administrador e a configuração de segurança desabilita CSRF; não exponha o sistema à internet sem rever esses controles.

O serviço opcional `fatecads/whatsapp-service/` não está incluído neste Compose. A recuperação de senha por WhatsApp não está disponível nesta configuração: o código atual aponta para `localhost:3000`, que dentro do container é o próprio container da aplicação. Esse fluxo exige integração com o serviço separado e uma sessão válida do WhatsApp; não é necessário para cadastrar contas ou fazer login.
