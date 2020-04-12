# cribbed liberally from Mait Raible's heroku deployment script found here: https://github.com/oktadeveloper/spring-boot-react-example/blob/master/heroku.sh
#!/bin/bash
set -e

cd `dirname $0`
r=`pwd`
echo $r

if [ -z "$(which heroku)" ]; then
  echo "You must install the Heroku CLI first!"
  echo "https://devcenter.heroku.com/articles/heroku-cli"
  exit 1
fi

if ! echo "$(heroku plugins)" | grep -q heroku-cli-deploy; then
  heroku plugins:install heroku-cli-deploy
fi

if ! echo "$(git remote -v)" | grep -q devcon-boot-server; then
  server_app=devcon-boot-server
  heroku create -r server $server_app
else
  server_app=$(heroku apps:info -r server --json | python -c 'import json,sys;print json.load(sys.stdin)["app"]["name"]')
fi
serverUri="https://$server_app.herokuapp.com"

./gradlew clean build -x test

mv build/libs/*jar build/libs/${server_app}.jar

heroku deploy:jar build/libs/*jar -r server -o "--server.port=\$PORT"
heroku config:set -r server FORCE_HTTPS="true"

# show apps and URLs
heroku open -r server