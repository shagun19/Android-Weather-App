const https = require('https')
const express = require('express');
const app = express();
const fetch = require('node-fetch');
const cors = require('cors')
const googleTrends = require('google-trends-api');

//const fs = require('fs');
//const key = fs.readFileSync('./key.pem');
//const cert = fs.readFileSync('./cert.pem');
//const server = https.createServer({key: key, cert: cert }, app);

app.get('/', (req, res) => {
  res
    .status(200)
    .send('Hello, world!')
    .end();
});
app.use(cors());

app.get('/latest', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26'; 
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/world', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/world?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all';
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/business', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/business?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all';
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/politics', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/politics?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all';
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/sport', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/sport?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all';
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/technology', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/technology?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all';
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/science', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/science?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all';
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/search/:query', async (request, response) => {
  const api_url = 'https://content.guardianapis.com/search?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all&q='+request.params.query;
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/detail-article/:articleId*', async (request, response) => {
  var path = request.params.articleId + request.params[0];
  const api_url = 'https://content.guardianapis.com/'+path+'?api-key=75cfee5a-fc9a-430f-b92a-c97a8e2dde26&show-blocks=all';
  const fetch_response = await fetch(api_url);
  const json = await fetch_response.json();
  response.json(json);
});

app.get('/googleTrends/:keyword',async (request, response) => {
  googleTrends.interestOverTime({keyword: request.params.keyword, startTime:  new Date('2019-06-01')}).then(function(results){
    var resMod = results.substring(1,results.length-1);
    var arr = JSON.parse(results)["default"]["timelineData"];
    var responseRet = [];
    for(i=0;i<arr.length;i++){
	responseRet.push(arr[i]["value"][0]);
    }
    response.json({"values":responseRet});
  }).catch(function(err){
       console.error('Oh no there was an error', err);
     });
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});

