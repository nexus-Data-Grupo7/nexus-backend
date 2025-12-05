#!/bin/bash
set -e

configurar_usuario() {
    local USUARIO=$1
    local DIRETORIO=$2
    local SENHA="urubu100"

    echo "--- Configurando usuário: $USUARIO para a pasta $DIRETORIO ---"

    if id "$USUARIO" &>/dev/null; then
        echo "Usuário $USUARIO já existe."
    else
        sudo useradd -m -s /bin/bash "$USUARIO"
        echo "Usuário $USUARIO criado com sucesso."
    fi

    echo "$USUARIO:$SENHA" | sudo chpasswd
    echo "Senha definida."

    if [ -d "$DIRETORIO" ]; then

        sudo chown -R "$USUARIO":"$USUARIO" "$DIRETORIO"
        
        sudo chmod -R 755 "$DIRETORIO"
        
        echo "Permissões aplicadas em '$DIRETORIO': $USUARIO tem controle total, outros apenas leitura."
    else
        echo "ALERTA: O diretório '$DIRETORIO' não existe. Crie a pasta antes."
    fi
    echo ""
}

echo ">>> INICIANDO CONFIGURAÇÃO DE AMBIENTE E PERMISSÕES <<<"

echo "Atualizando pacotes..."
sudo apt-get update && sudo apt-get upgrade -y

if ! command -v docker &> /dev/null
then
    echo "Instalando Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh && sudo sh get-docker.sh
    rm get-docker.sh 
else
    echo "Docker já instalado."
fi

echo "Iniciando serviço Docker..."
sudo systemctl start docker
sudo systemctl enable docker

echo ">>> APLICANDO REGRAS DE ACESSO <<<"

configurar_usuario "backend" "jar"

configurar_usuario "dbuser" "mysql"

configurar_usuario "frontend" "node"

echo "--> Iniciando containers..."

if [ -f "docker-compose.yml" ]; then
    sudo docker compose -f docker-compose.yml up --build -d
    echo "Containers iniciados."
    echo "Status:"
    sudo docker compose -f docker-compose.yml ps
else
    echo "ERRO: docker-compose.yml não encontrado nesta pasta."
fi

echo "--> Carregando/Atualizando o agendamento do cron..."
sudo crontab /home/ubuntu/infraestrutura/trabalho_agendado.cron
echo "Agendamento do cron carregado a partir de trabalho_agendado.cron"

echo "" 
echo ">>> CONFIGURAÇÃO CONCLUÍDA <<<"