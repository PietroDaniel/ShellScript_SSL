#!/bin/bash

# Função para validar a entrada do usuário
validate_input() {
  if [[ -z "$1" ]]; then
    echo "Erro: Entrada inválida. Por favor, preencha todos os campos."
    exit 1
  fi
}

# Solicitação de informações necessárias para o certificado
echo "== Geração de Certificado SSL Autoassinado =="
read -p "Digite o nome do domínio ou IP: " domain
validate_input "$domain"

read -p "Digite o nome da organização: " organization
validate_input "$organization"

read -p "Digite a cidade: " city
validate_input "$city"

read -p "Digite o estado: " state
validate_input "$state"

read -p "Digite o país (código de dois dígitos): " country
validate_input "$country"

# Geração do certificado autoassinado usando OpenSSL
echo "Gerando o certificado..."
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout server.key -out server.crt \
  -subj "/C=$country/ST=$state/L=$city/O=$organization/CN=$domain"

# Verificação do sucesso na geração do certificado
if [ $? -eq 0 ]; then
  echo "Certificado gerado com sucesso!"
  chmod 600 server.key
else
  echo "Erro ao gerar o certificado."
  exit 1
fi
