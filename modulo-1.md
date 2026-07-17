# Módulo 1: O Mínimo Necessário sobre Práticas em Java

Olá! Que bom ter você aqui. Sou seu professor e, com mais de 20 anos de experiência em engenharia de software, já vi muita tecnologia nascer e morrer. Mas sabe o que permanece? **Os fundamentos.** 

Neste primeiro módulo, nós não vamos apenas focar em decorar sintaxe. Vamos entender *como* e *por que* usamos certas práticas no desenvolvimento Java moderno, especialmente ao construirmos sistemas reais e escaláveis, como o nosso Abertamente CMS.

Antes de mergulharmos fundo, me responda mentalmente: *Você já se sentiu perdido com termos como "Injeção de Dependência" ou "Encapsulamento"?* Se sim, fique tranquilo. Vamos desmistificar isso agora, de forma objetiva e direta.

---

## 1. Orientação a Objetos no Mundo Real

A Orientação a Objetos (OO) não é apenas um conceito acadêmico; é como nós organizamos o caos em projetos grandes. 

*   **Classes e Objetos:** Pense na Classe como a planta de uma casa (a definição) e o Objeto como a casa construída (a instância na memória).
*   **Encapsulamento:** É proteger os dados. Em Java, deixamos os atributos `private` e usamos métodos (como getters e setters, ou métodos de negócio) para alterá-los. *Dica de Ouro:* Nunca exponha o estado interno do seu objeto sem necessidade. Se um atributo não deve ser alterado após a criação, não crie um setter para ele!
*   **Herança:** Serve para reaproveitar comportamentos e atributos comuns.
    *   **Exemplo Prático:** No nosso CMS, teremos várias tabelas (Posts, Usuários, Comentários) que sempre terão um "ID" e uma "Data de Criação". Em vez de repetir isso em todo lugar, criamos uma classe mãe chamada `BaseEntity` e fazemos as outras classes herdarem dela (`extends BaseEntity`).

> **💡 Para Refletir:** Você consegue imaginar o retrabalho de adicionar um campo "Data de Atualização" em 50 tabelas se não usássemos herança para uma `BaseEntity`? 

### Classe Abstrata vs Interface
Muitos iniciantes confundem esses dois. Vamos esclarecer usando o nosso projeto:

*   **Classe Abstrata (`abstract class`):** É um molde incompleto. Você **não pode** dar um `new BaseEntity()`, porque uma entidade base genérica não faz sentido sozinha. Ela só serve para ser herdada. Usamos classe abstrata quando queremos fornecer código pronto (atributos e métodos com lógica) para os filhos herdarem.
*   **Interface (`interface`):** É um "contrato". Uma interface não tem código pronto (em sua regra geral), ela apenas diz *o que* deve ser feito, mas não *como*. No nosso CMS, os repositórios (ex: `PostRepository`) são interfaces do Spring Data JPA. O contrato diz que deve haver um método para salvar um post, e o Spring escreve a implementação por debaixo dos panos para nós.

**Quando usar qual?** 
Use **Classe Abstrata** quando as classes filhas compartilharem *estado* (variáveis) e *comportamento* base (ex: ID e data de criação em entidades).
Use **Interface** quando quiser definir um contrato rigoroso de ações que classes totalmente diferentes devem seguir (ex: um `NotificacaoService` pode ter implementações diferentes para envio via Email ou SMS, ambas assinando o mesmo contrato).

### Sobrecarga, Sobrescrita e Polimorfismo
Essas três palavras costumam assustar, mas são simples:

*   **Sobrecarga (Overloading):** É ter vários métodos com o **mesmo nome, mas parâmetros diferentes** na mesma classe. 
    *   *Exemplo:* Um método `buscarPost(Long id)` e outro `buscarPost(String titulo)`. Ambos buscam, mas usam dados de entrada diferentes.
*   **Sobrescrita (Overriding):** É quando a classe filha decide mudar o comportamento de um método da classe mãe. Usamos a anotação `@Override`. 
    *   *Exemplo:* A mãe diz como imprimir dados na tela, mas o filho precisa imprimir de um jeito mais específico.
*   **Polimorfismo:** Vem do grego "muitas formas". É a capacidade de tratar um objeto como se fosse do tipo mais genérico (pai ou interface), permitindo que o programa decida em tempo de execução qual comportamento usar.
    *   *Exemplo:* Se você tem uma lista de `BaseEntity`, pode chamar um método genérico `obterId()` e cada objeto real (seja ele um `Post` ou `Usuario`) responderá corretamente.

---

## 2. Injeção de Dependência e Acoplamento

Aqui nós separamos os juniores dos seniores. No Spring Framework, a Injeção de Dependência (DI) é o coração da arquitetura. Mas primeiro, o que é acoplamento?

**Acoplamento** é o grau de dependência entre duas classes. Se a classe `PostController` cria diretamente o `PostService` usando `new PostService()`, dizemos que elas estão fortemente acopladas. Se o construtor do Service mudar, o Controller quebra. Para evitar isso, nós *injetamos* a dependência (entregamos ela pronta por fora) em vez de criá-la.

### O que é e para que serve o `@Autowired`?
O `@Autowired` é a forma de dizer ao Spring: *"Ei, eu preciso de um objeto desse tipo aqui, por favor, encontre-o no seu contexto e injete-o para mim"*. 

Muitos tutoriais antigos ensinam a usar a anotação `@Autowired` direto nos atributos (Field Injection):

```java
// ❌ NÃO FAÇA ISSO EM CÓDIGO NOVO!
@Service
public class PostService {
    @Autowired
    private PostRepository repository;
}
```

**Por que evitar o `@Autowired` em propriedades?**
1. Esconde as dependências da classe (dificulta ver o quanto a classe está acoplada, podendo virar uma classe "monstro" com 20 dependências silenciosas).
2. Torna quase impossível fazer Testes Unitários sem levantar o contexto do Spring inteiro, pois você não consegue "injetar" um mock no atributo privado facilmente.

**A Solução (Padrão de Mercado): Injeção via Construtor.**

```java
// ✅ PADRÃO SÊNIOR
@Service
public class PostService {
    
    private final PostRepository repository; // Note o 'final'. Imutável!

    // O Spring automaticamente injeta a dependência aqui. Não precisa do @Autowired nas versões mais recentes!
    public PostService(PostRepository repository) {
        this.repository = repository;
    }
}
```

> **📚 Referência Rápida:** O próprio time do Spring recomenda a Injeção por Construtor, pois garante que as dependências obrigatórias estejam presentes no momento da criação do objeto, evitando `NullPointerException` e permitindo testes limpos.

---

## 3. Tipos de Dados e Enums (Máquinas de Estado)

Strings são perigosas quando representam estados. Imagine que o status de um post no blog pode ser "RASCUNHO" ou "PUBLICADO". Se você usar uma `String`, alguém pode acidentalmente salvar "RASCUNHOO" (com dois 'O's) no banco de dados, quebrando sua lógica e sujando seu banco.

Para garantir a integridade, usamos **Enums**.

```java
public enum PostStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
```

**Por que usar Enums?**
*   **Segurança de Tipo (Type-safety):** O compilador do Java não vai deixar você passar um valor inválido. Você é obrigado a usar `PostStatus.DRAFT`.
*   **Controle de Máquina de Estados:** No nosso CMS, a regra de negócio dita que um Post só pode ir para a tela principal se for `PostStatus.PUBLISHED`. Usando Enums (e.g., `Role.ADMIN`, `Role.USER`), garantimos que o sistema reconheça estritamente os estados e perfis pré-definidos, sem espaço para ambiguidades.

---

## 4. Padrões de Nomenclatura e Clean Code

O código é lido muito mais vezes do que é escrito. Escrever código limpo (*Clean Code*) é uma demonstração de respeito com o seu time (e com você mesmo no futuro).

**Regras Inquebráveis em Java:**

1.  **PascalCase para Classes e Interfaces:** 
    *   Certo: `PostController`, `UserRepository`, `BaseEntity`.
    *   Errado: `postController`, `User_Repository`.
2.  **camelCase para Variáveis, Atributos e Métodos:**
    *   Certo: `findByEmail()`, `postStatus`, `createdAt`.
    *   Errado: `FindByEmail()`, `post_status`.
3.  **Nomes Semânticos:** Esqueça variáveis como `int x` ou `String a`. 
    *   Use nomes que gritam o seu propósito: `int totalActiveUsers`, `String authorName`.

> **🗣️ Pergunta do Professor:** Quando você lê um método chamado `process()`, você sabe exatamente o que ele faz sem olhar a implementação? Provavelmente não. Agora, e se ele se chamar `publishDraftPost()`? Bem melhor, não é? Nomes de métodos devem ser verbos de ação claros!

### SOLID: O Princípio Aberto / Fechado (Open/Closed Principle)
Para fechar com chave de ouro, preciso falar de um dos princípios do SOLID, o **Aberto/Fechado (OCP)**. Ele diz que "uma entidade de software deve estar ABERTA para extensão, mas FECHADA para modificação".

O que isso significa na prática? Se amanhã o nosso CMS precisar exportar posts em formato PDF além do formato HTML, não devemos ir na classe que exporta HTML e colocar um monte de `if (formato == "PDF")`. Isso cria acoplamento e quebra a classe original que já funcionava. 

O ideal é usar as **Interfaces** que aprendemos acima. Criamos um contrato `PostExporter` e duas classes isoladas: `HtmlPostExporter` e `PdfPostExporter`, que implementam o contrato. Assim, adicionamos novas funcionalidades apenas "estendendo" o comportamento (criando novas classes), sem modificar o código que já estava pronto e testado.

---

### Resumo da Aula

Neste módulo, estabelecemos nossa fundação. Entendemos que a **Herança e Classes Abstratas** evitam repetição, e que as **Interfaces** definem contratos rigorosos. Vimos como a **Injeção via Construtor** diminui o acoplamento de forma superior ao `@Autowired` em campos. Consolidamos o uso de **Enums** para blindar nossos estados e aprendemos o básico de Polimorfismo e SOLID (Open/Closed Principle) para tornar a arquitetura robusta.

**Para testar seus conhecimentos:** Você conseguiu compreender a diferença entre sobrescrita (override) e sobrecarga (overload)? 

Se esse conceito ficou claro, você já está um passo à frente da maioria!

**Próximo Passo:** No Módulo 2, vamos desvendar como o ecossistema do Spring Boot pega essas práticas de Java e constrói a "mágica" de Inversão de Controle por trás dos panos. Até lá!
