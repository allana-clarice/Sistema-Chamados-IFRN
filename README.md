
# Sistema de Chamados do IFRN

API REST para abertura e acompanhamento de chamados de suporte (TI, manutenção e infraestrutura) do IFRN. Um solicitante abre o chamado, o técnico do setor responsável assume, registra o andamento e encerra, e todo o histórico fica salvo.

Projeto individual da disciplina de Desenvolvimento de Sistemas Corporativos (TADS, 2026.2, prof. Jeferson Queiroga).

## Tecnologias

Java 21, Spring Boot 4, Spring Web, Spring Data JPA, PostgreSQL 17, Flyway, Lombok, Maven e Docker.

## Como executar

Só é preciso ter o Docker instalado.


```

bash
git clone [https://github.com/SEU-USUARIO/Sistemas-chamados-ifrn.git](https://github.com/SEU-USUARIO/Sistemas-chamados-ifrn.git)
cd Sistemas-chamados-ifrn
docker compose up --build

```

Quando o log mostrar `Started SistemasChamadosIfrnApplication`, a API está em `http://localhost:8080`. O banco sobe junto, com volume persistente, e as migrations criam as tabelas e os dados iniciais (2 setores, 4 categorias e 4 usuários).

Para parar e apagar o banco e recomeçar do zero:


```

bash
docker compose down -v

```

### Rodando fora do Docker

Suba só o banco e informe a senha por variável de ambiente (ela não existe no código):


```

bash
docker compose up -d postgres
export SPRING_DATASOURCE_PASSWORD=postgresifrn      # PowerShell: $env:SPRING_DATASOURCE_PASSWORD="postgresifrn"
mvn spring-boot:run

```

O banco fica exposto na porta 5433 da máquina para não conflitar com um PostgreSQL local.

## Dados iniciais

| Usuário | Tipo | Setor |
|---|---|---|
| 1 Ana Solicitante | SOLICITANTE | - |
| 2 Carlos Solicitante | SOLICITANTE | - |
| 3 João Técnico TI | TECNICO | COTIC |
| 4 Maria Técnica Manutenção | TECNICO | CMINF |

As categorias 1 e 2 pertencem ao COTIC e as 3 e 4 ao CMINF.

## Endpoints

| Recurso | Rotas |
|---|---|
| Setores | `POST`, `GET` (lista e por id), `PUT`, `DELETE` em `/setores` e `GET /setores/{id}/categorias` |
| Categorias | `POST`, `GET` (lista e por id), `PUT`, `DELETE` em `/categorias` |
| Usuários | `POST`, `GET` (lista e por id), `PUT` em `/usuarios` e `GET /usuarios/{id}/chamados` |
| Chamados | `POST /chamados`, `GET /chamados`, `GET /chamados/{id}` |
| Ações | `PATCH /chamados/{id}/assumir`, `/resolver`, `/cancelar` e `POST /chamados/{id}/interacoes` |

Como não há autenticação, quem executa a ação vai no corpo da requisição (`solicitanteId`, `tecnicoId` ou `autorId`).

### Paginação e filtros

Todas as listagens são paginadas (`page`, `size`, `sort`), com 10 itens por padrão e máximo de 50. A resposta traz `itens`, `numeroPagina`, `tamanho`, `totalElementos` e `totalPaginas`. Os filtros são opcionais e combináveis:

| Listagem | Filtros |
|---|---|
| `/setores` | `nome` |
| `/categorias` | `nome`, `setorId` |
| `/usuarios` | `nome`, `tipo`, `setorId` |
| `/chamados` | `status`, `prioridade`, `setorId`, `categoriaId`, `solicitanteId`, `tecnicoId`, `dataInicio`, `dataFim` |
| `/usuarios/{id}/chamados` | `status` |

### Erros

Todo erro devolve o mesmo JSON, montado em um `@RestControllerAdvice`:


```

json
{
"timestamp": "2026-10-14T21:05:12.345",
"status": 409,
"mensagem": "O técnico pertence a um setor diferente da categoria deste chamado.",
"caminho": "/chamados/1/assumir"
}

```

`400` para dados inválidos, `404` para recurso inexistente e `409` para violação de regra de negócio.

## Exemplos de requisição


```

bash

# Abrir chamado

curl -X POST http://localhost:8080/chamados -H "Content-Type: application/json"

-d '{"titulo":"Wi-Fi caiu","descricao":"Sem sinal no lab","local":"Lab 01","solicitanteId":1,"categoriaId":1}'

# Listar chamados abertos do setor 1, mais recentes primeiro

curl "http://localhost:8080/chamados?status=ABERTO&setorId=1&page=0&size=10&sort=dataAbertura,desc"

# Técnico assume o chamado

curl -X PATCH http://localhost:8080/chamados/1/assumir -H "Content-Type: application/json"

-d '{"tecnicoId":3}'

# Técnico resolve

curl -X PATCH http://localhost:8080/chamados/1/resolver -H "Content-Type: application/json"

-d '{"tecnicoId":3,"solucao":"Cabo de rede substituído."}'

```

No PowerShell, use `curl.exe` e troque as aspas simples do JSON por aspas duplas com escape, ou utilize o arquivo `requisicoes.http` (que já possui todas as requisições prontas para rodar direto no VS Code ou IntelliJ).

## Estrutura do projeto


```

src/main/java/com/example/sistemaschamadosifrn
├── controller   recebem a requisição, chamam o service e devolvem a resposta
├── service      regras de negócio e exceções próprias
├── repository   Spring Data JPA (consultas com @Query)
├── model        entidades JPA e enums
├── dto          objetos de entrada e saída
├── exception    exceções e tratamento global de erros
└── util         apoio aos filtros de texto
src/main/resources/db/migration   migrations do Flyway

```

## Migrations com Flyway

O esquema do banco é criado pelas migrations em `src/main/resources/db/migration` (`V1__criar_tabelas.sql`, `V2__inserir_dados_iniciais.sql`). O Hibernate não cria nada: com `spring.jpa.hibernate.ddl-auto=validate` ele só confere se as tabelas batem com as entidades.

* **Como o Flyway registra as versões:** Na primeira execução ele cria a tabela `flyway_schema_history`. Para cada migration aplicada ela guarda versão, descrição, nome do arquivo, checksum, quem executou, quando e se deu certo. Nas execuções seguintes o Flyway compara os arquivos da pasta com esse histórico e roda apenas as versões que ainda não estão registradas, em ordem.
* **Se o checksum de uma migration já aplicada mudar:** O Flyway recalcula o checksum dos arquivos ao iniciar e, se algum for diferente do registrado, a validação falha com `checksum mismatch` e a aplicação não sobe. Isso protege o banco de ficar diferente do que o código espera. Por isso migration aplicada nunca é editada: a correção entra em uma nova versão (`V3__...`).
* **Por que `ddl-auto=update` não é usado em produção:** Com ele o Hibernate altera o esquema por conta própria, sem histórico nem revisão. Ele não renomeia nem remove colunas com segurança, pode dar resultados diferentes em cada ambiente e pode causar perda de dados. Com migrations o esquema é versionado, revisável e igual em qualquer máquina.

## Regras de negócio

Ficam todas nos services (nenhuma no controller):

1. O chamado nasce `ABERTO`, com `dataAbertura` preenchida pelo sistema e prioridade `MEDIA` quando não informada.
2. Só `SOLICITANTE` abre chamado.
3. O técnico só assume chamado `ABERTO` de uma categoria do seu setor; o status vai para `EM_ATENDIMENTO`.
4. Só o técnico atribuído resolve, a solução é obrigatória e `dataFechamento` é preenchida.
5. Só o próprio solicitante cancela, e apenas enquanto o chamado está `ABERTO`.
6. Chamado `RESOLVIDO` ou `CANCELADO` não aceita alterações nem novas interações.
7. Só o solicitante e o técnico atribuído registram interações.
8. Setor ou categoria com chamados vinculados não pode ser excluído.

```