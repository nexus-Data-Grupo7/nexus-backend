FROM node:20-slim

WORKDIR /usr/src/app

RUN apt-get update && apt-get install -y git

RUN git clone https://github.com/nexus-Data-Grupo7/nexus-frontend.git

WORKDIR /usr/src/app/nexus-frontend/web-data-viz

RUN npm install

EXPOSE 3333

CMD [ "npm", "start" ]