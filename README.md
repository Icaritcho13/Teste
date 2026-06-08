# Concorrência e Consistência em Banco de Dados com Spring Boot

Trabalho prático sobre o problema da **Atualização Perdida (Lost Update)** em sistemas transacionais e sua solução com **controle de versão otimista (`@Version`)** usando JPA/Hibernate.

## Entrega

- **Repositório GitHub:** https://github.com/Icaritcho13/Teste

## Integrantes e divisão

| Integrante | Responsabilidade |
|---|---|
| Ícaro Farias | Parte 1 — Cenário sem bloqueio (`ContaBancaria`) |
| Ícaro Mac-Culloch | Parte 2 — Solução com `@Version` (`ContaBancariaVersionada`) |

## Pré-requisitos

- **JDK 17** (ou superior)
- **Maven 3.8+** (ou abra o projeto numa IDE com suporte a Maven)
- **Apache JMeter 5.x** (para os testes de concorrência)

## Como rodar a aplicação

```bash
# Na raiz do projeto
mvn spring-boot:run
```

Ou abra o projeto na IDE (IntelliJ, Eclipse, VS Code com Extension Pack for Java) e execute a classe `ConcorrenciaBancoApplication`.

A aplicação sobe em `http://localhost:8080`.

### Banco H2 (em memória)

O H2 sobe junto com a aplicação. Para inspecionar o saldo em tempo real:

1. Acesse `http://localhost:8080/h2-console`
2. No campo **JDBC URL**, use: `jdbc:h2:mem:testdb`
3. Usuário: `sa` — Senha: (em branco)
4. Conecte e rode, por exemplo: `SELECT * FROM conta_bancaria;` ou `SELECT * FROM conta_bancaria_versionada;`

Duas contas já são criadas na inicialização (ver `src/main/resources/import.sql`), ambas com `id = 1` e saldo `1000.00`.

> Observação: o banco é em memória e **zera a cada reinício** da aplicação. Cada execução começa do estado limpo.

## Endpoints

### Parte 1 — sem controle de concorrência
- `POST /contas/{id}/deposito?valor=10.00`
- `POST /contas/{id}/saque?valor=10.00`

### Parte 2 — com `@Version`
- `POST /contas-versionadas/{id}/deposito?valor=10.00`
- `POST /contas-versionadas/{id}/saque?valor=10.00`

Teste rápido de uma requisição:

```bash
curl -X POST "http://localhost:8080/contas/1/deposito?valor=10.00"
```

### Respostas de erro (centralizadas em `ApiExceptionHandler`)

| Situação | Status |
|---|---|
| Conta inexistente | `404 Not Found` |
| Valor não positivo / saldo insuficiente | `400 Bad Request` |
| Conflito de concorrência otimista (Parte 2) | `409 Conflict` |

## Como rodar os testes de concorrência (JMeter)

O plano está na raiz: **`teste-concorrencia.jmx`**. Ele contém dois Thread Groups:

- **PARTE 1 - /contas (sem trava)** — habilitado por padrão
- **PARTE 2 - /contas-versionadas (@Version)** — desabilitado por padrão

Configuração de cada grupo: **100 threads × 10 loops = 1000 depósitos** de `R$ 1,00` na conta `id = 1`.

**Passo a passo:**

1. Suba a aplicação (`mvn spring-boot:run`).
2. Abra o JMeter e carregue `teste-concorrencia.jmx`.
3. **Parte 1:** com o grupo da Parte 1 habilitado, clique em *Start*. Ao terminar, confira o saldo no H2 console e salve o print do *Relatorio Resumo*.
4. **Reinicie a aplicação** (para zerar o saldo).
5. **Parte 2:** desabilite o grupo da Parte 1, habilite o grupo da Parte 2 e rode novamente. Confira o saldo, a coluna `version` e os códigos `409` na *Arvore de Resultados*.

> Variáveis configuráveis no topo do plano (User Defined Variables): `HOST`, `PORT`, `CONTA_ID`, `VALOR`.

> **Nota sobre depósitos vs. saques:** o roteiro menciona depósitos *e* saques. O plano usa **depósitos puros** de propósito, porque assim o saldo final esperado é determinístico (`1000 + 1000 × 1,00 = 2000,00`) e o erro fica visualmente inegável. Para incluir saques, duplique o sampler dentro do Thread Group apontando para `/saque`.

---

## Relatório de Conclusão

### Cenário do teste
- Saldo inicial: **R$ 1.000,00**
- Operações: **1000 depósitos** de **R$ 1,00**
- Saldo final esperado: **R$ 2.000,00**

### Parte 1 — sem controle de concorrência (valores ilustrativos)
- Respostas `200 OK`: **1000** (todas "passam")
- Respostas de erro: **0**
- Saldo final observado: **R$ 1.453,00**
- Diferença em relação ao esperado: **− R$ 547,00**
- Print do *Relatorio Resumo* do JMeter: _‹inserir›_
- Print do H2 console: _‹inserir›_

**Análise:** todas as 1000 requisições retornaram sucesso, mas o saldo final ficou **muito abaixo** do esperado. Várias transações leram o mesmo saldo antes de outra gravar, e a última escrita sobrescreveu as anteriores (Lost Update). O banco executou cada comando corretamente — a inconsistência surge da leitura obsoleta no nível da aplicação, não do SGBD.

### Parte 2 — com `@Version` (valores ilustrativos)
- Respostas `200 OK`: **420**
- Respostas `409 Conflict`: **580**
- Saldo final observado: **R$ 1.420,00**  (= 1000 + 420 operações aceitas)
- Coluna `version` no banco: **420**
- Print do *Relatorio Resumo* / *Arvore de Resultados* do JMeter: _‹inserir›_
- Print do H2 console (com a coluna `version`): _‹inserir›_

**Análise:** o saldo final ficou **perfeitamente consistente** com o número de operações aceitas (1000 + 420 = 1420), e a coluna `version` confirma exatamente 420 atualizações aplicadas. O `@Version` faz o Hibernate rejeitar a escrita perdedora de cada conflito, lançando `ObjectOptimisticLockingFailureException` (tratada como `409 Conflict`). Nenhuma atualização foi silenciosamente perdida.

### Comparação e trade-off

| Critério | Parte 1 (sem trava) | Parte 2 (`@Version`) |
|---|---|---|
| Todas as operações são aplicadas? | Sim (todas `200`) | Não — algumas viram `409` |
| Saldo final | Inconsistente (R$ 1.453,00) | Consistente (R$ 1.420,00 = 1000 + aceitas) |
| Atualizações perdidas silenciosamente | Sim | Nenhuma |
| Mecanismo de defesa | Nenhum | Rejeita a escrita perdedora |

**Conclusão:** o controle otimista **não conclui** a operação concorrente — ele a **rejeita** (`409`), garantindo consistência ao custo de operações descartadas. A Parte 1 aceita tudo mas corrompe o saldo; a Parte 2 protege o saldo mas exige que o cliente reexecute (retry) as requisições que receberam `409`. Em um sistema de produção, esse retry completaria o padrão.
