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

### Linux (somente para o Cofre)
2. Se compile.sh não for executável, usar `chmod +x compile.sh`
3. Rode `./compile.sh` 

### Windows (Cofre e LogRead)
2. Se o seu maven estiver no wsl, rode `.\w_compile.bat`

## Diretamente com o maven
1. No mesmo diretório que o pom.xml,i.e. Cofre/ ou LogRead/, rode `mvn clean package`

# Rodando o jar
> [!WARNING]
> - Os .jar a serem executados sao os `with-dependencies`
> - O banco de dados `cofre.db` sera criado no mesmo diretorio em que o jar do CofreDigital for excutado
> - Os 2 .jar devem estar no mesmo diretorio e os diretorio `Files/` e `Keys/` devem estar no mesmo diretorio que os jar

## Usando o script
1. Vá para o diretório `scripts`
2. Se run.sh não for executável, usar `chmod +x run.sh`
3. Rode `./run.sh`

## Diretamente
### CofreDigital
1. Rode `java -jar CofreDigital-1.0-SNAPSHOT-jar-with-dependencies.jar`
### LogRead
1. Rode `java -jar LogReader-1.0-SNAPSHOT-jar-with-dependencies.jar cofre.db`
