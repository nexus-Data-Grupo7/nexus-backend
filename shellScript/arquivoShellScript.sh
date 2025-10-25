#!/bin/bash
# Função para verificar se o Java está instalado
verificar_java() {
    echo "🔍 Verificando instalação do Java..."
    if command -v java &> /dev/null; then
        echo "Java já está instalado."
        java -version
        exit 0
    else
        echo "Java não está instalado neste sistema."
    fi
}

# Função para instalar o Java
instalar_java() {
    echo "Iniciando instalação do Amazon Corretto 21..."
    sudo apt update -y
    sudo apt install -y wget software-properties-common

    echo "Adicionando repositório Amazon Corretto..."
    wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add -
    sudo add-apt-repository 'deb https://apt.corretto.aws stable main'

    echo "Instalando Java 21..."
    sudo apt update -y
    sudo apt install -y java-21-amazon-corretto-jdk

    echo "Java instalado com sucesso!"
    java -version

    # Configurando variáveis de ambiente
    echo "Configurando variáveis de ambiente..."
    JAVA_PATH=$(readlink -f $(which java) | sed 's:/bin/java::')
    
    echo "export JAVA_HOME=$JAVA_PATH" | sudo tee -a /etc/profile.d/java.sh
    echo 'export PATH=$JAVA_HOME/bin:$PATH' | sudo tee -a /etc/profile.d/java.sh
    sudo chmod +x /etc/profile.d/java.sh

    echo "Atualizando variáveis de ambiente..."
    source /etc/profile.d/java.sh

    echo "Instalação e configuração concluídas!"
}

# Função principal
main() {
    verificar_java

    read -p "Deseja instalar o Java Corretto 21 agora? (s/n): " resposta
    case "$resposta" in
        s|S|sim|SIM)
            instalar_java
            ;;
        *)
            echo " Instalação cancelada pelo usuário."
            exit 1
            ;;
    esac
}

# Executa o script principal
main
