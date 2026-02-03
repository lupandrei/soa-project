# Pasul 1: Preluăm watchdog-ul oficial OpenFaaS
FROM ghcr.io/openfaas/classic-watchdog:0.2.1 as watchdog

# Pasul 2: Folosim o imagine de Python slim
FROM python:3.11-slim

# Copiem watchdog-ul în containerul nostru
COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

WORKDIR /home/app
COPY handler.py .

# Configurăm comanda pe care watchdog o va executa la fiecare request
ENV fprocess="python3 handler.py"
# Portul pe care ascultă watchdog (standard OpenFaaS)
EXPOSE 8080

HEALTHCHECK --interval=3s CMD [ -e /tmp/.lock ] || exit 1

# Modul HTTP îi spune watchdog-ului să paseze body-ul către STDIN
ENV mode="http"

CMD ["fwatchdog"]