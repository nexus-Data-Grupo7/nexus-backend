# Intrução para utilização

## Config .env
- `Atualize todas as variaveis de ambiente AWS do .env com suas credenciais todas vez que logar em sua EC2`

## Config EC2
- `Crie uma EC2 com o t3 medium`
- `Habilite o IAM de sua EC2`
- `Atulize os grupos e segurança para a utilização de portas públicas`

## ** Faca as altercoes no comando abaixo para que tudo funcione **

## Execute este comando para clonar este arquivo para sua EC2
- `scp -i "C:\Users\ventu\OneDrive\Documentos\Sptech\Instrodução a sistemas operacionais\chave aws\grupo7.pem" -r C:\Users\ventu\OneDrive\Documentos\Sptech\ProjetoPI\GitBash\Nexus\nexus-backend\infraestrutura ubuntu@3.89.207.168:~/`

## Dê a permissão para executar o arquivo
- `chmod +x infraestrutura/setup.sh`

## Execute o arquivo
- `sudo ./infraestrutura/setup.sh`

## Acessando os Serviços
- **Front-end (Node.js):** `http://<IP_DO_SERVIDOR>:3333`
- **Back-end (Java/JAR):** `http://<IP_DO_SERVIDOR>:8080`

## Para ver logs do Cron
- `sudo tail -f /var/log/processar_planilha.log`