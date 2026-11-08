# Sistema Web de Gestão de Biblioteca (Spring Boot MVC)

Este projeto se baseia em um sistema web desenvolvido para automatizar a gestão e empréstimos de livros, estruturado na arquitetura Spring MVC.

---

## 1. Escopo do Sistema

O sistema é constituido de 4 tabelas fundamentais:
- **Autor:** Cadastro e gestão dos autores das obras.
- **Livro:** Controle do acervo de livros da biblioteca (vinculado a um Autor).
- **Leitor:** Gestão dos usuários/leitores cadastrados.
- **Empréstimo:** Registro de saídas, devoluções e status das obras para os leitores.

---

## 2. Regras de Negócio (RN)

1. **RN01 - Vínculo de Autor:** Todo livro deve obrigatoriamente possuir um autor válido cadastrado.
2. **RN02 - Limite de Empréstimos:** Um leitor pode ter no máximo 3 empréstimos ativos simultaneamente.
3. **RN03 - Prazo de Devolução:** O prazo padrão para devolução do livro é de 14 dias a contar da data de empréstimo.
4. **RN04 - Bloqueio de Empréstimo:** Leitura/empréstimo não pode ser realizado para leitores com pendências ou atrasos.
5. **RN05 - Disponibilidade de Exemplar:** Um livro só pode ser emprestado se estiver com status "Disponível".

---

## 3. Validação Cruzada de Consistência

Foi realizada a revisão dos elementos da 1ª Etapa garantindo o alinhamento entre as partes:
- **Model / MER / Protótipo:** Todos os atributos definidos no modelo conceitual (ex: `CPF` e `Email` em Leitor; `ISBN` em Livro) estão refletidos no Diagrama de Classes e nos protótipos de tela.
- **Relacionamentos:** A cardinalidade (ex: 1 Autor para N Livros, 1 Leitor para N Empréstimos) está consistente entre as entidades JPA e as tabelas relacionais.

---

## Integrantes e Responsabilidades

- **Integrante 1:** Gestão do Repositório, Documentação e Modelo MER
- **Integrante 2:** Diagrama de Classes UML (Camada Model)
- **Integrante 3:** Prototipagem de Telas do Acervo (Livro e Autor)
- **Integrante 4:** Prototipagem de Telas Operacionais (Leitor e Empréstimo)
- **Integrante 5:** Escopo, Regras de Negócio e Validação Cruzada