#### Kafka Client
### 프로젝트 개요
# Kafka, Hbase, Spark 등의 오픈소스 소프트웨어(OSS)를 이해하기 위한 파일럿 프로젝트의 클라이언트 소스코드 입니다. 
# 사용자가 입력한 키워드에 대해서 7일 이내의 해당 트윗들을 분석하여 관련 키워드를 시각화합니다.
# 트윗은 매분 Kafka Cluster로 전송이 되며 실시간으로 분석하여 시각화된 차트에 반영합니다.

### Producer Client
## 사용기술
# Kafka Producer API
# Twitter API Search 기능
# Scheduler 라이브러리 - Quartz
# 형태소 분석 라이브러리 - KoNLP

### Consumer Client
## 사용기술
# Kafka Consumer API
# Hadoop
# HBase
# Spark

### NodeJS Web Server
## 사용기술
# NodeJS
# d3.js

## 시각화 모델 후보
# [Word Cloud](https://www.jasondavies.com/wordcloud/)
# [Galaxy Chart](https://risacher.org/galaxy-chart/)
