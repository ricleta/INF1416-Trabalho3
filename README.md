# INF1416-Trabalho3
Trabalho 3 de INF1416 de 2025.1

# Autores
- Lívia Lutz dos Santos, 2211055
- Ricardo Bastos Leta Vieira, 2110526

# Compilação
> [!WARNING]
> Tenha certeza de ter o Maven da Apache instalado para a compilação
> [Documentação oficial para instalar](https://maven.apache.org/install.html)

## Usando o script
1. Vá para o diretório `scripts`
2. Se compile.sh não for executável, usar `chmod +x compile.sh`
3. Rode `./compile.sh`

## Diretamente com o maven
1. No mesmo diretório que o pom.xml, rode `mvn clean package`

# Rodando o jar
## Usando o script
1. Vá para o diretório `scripts`
2. Se run.sh não for executável, usar `chmod +x run.sh`
3. Rode `./run.sh`

## Diretamente
1. Rode `java -jar target/CofreDigital-1.0-SNAPSHOT-jar-with-dependencies.jar`
