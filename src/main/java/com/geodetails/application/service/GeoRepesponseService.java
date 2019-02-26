package com.geodetails.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.geodetails.application.model.ResponseResultModel;

@Service
public class GeoRepesponseService {

	Logger logger = LoggerFactory.getLogger(GeoRepesponseService.class);

	@Value("${mockservice.url}")
	String mockserviceUrl;

	public Callable<String> getGeoDetails(String ip) throws InterruptedException {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				RestTemplate restTemplate = new RestTemplate();
				String resMsg = null;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				Map<String, String> inpt = new HashMap<String, String>();
				inpt.put("ip", ip);
				logger.info(" mockserviceUrl={}  ip={}", mockserviceUrl,ip);
				ResponseEntity<ResponseResultModel> responseEntity = restTemplate.postForEntity(mockserviceUrl, inpt,
						ResponseResultModel.class);
				ResponseResultModel responseResultModel = responseEntity.getBody();
				logger.info("after post2 res=" + responseResultModel.getResults());
				String addrs[] = null;
				if (responseResultModel.getResults() != null && responseResultModel.getResults().length > 0) {
					if (responseResultModel.getResults()[0].getFormattedAddressLines() != null
							&& responseResultModel.getResults()[0].getFormattedAddressLines().length > 2) {
						addrs = responseResultModel.getResults()[0].getFormattedAddressLines();
						resMsg = addrs[0] + "," + addrs[1] + "," + addrs[2];
					} else {
						resMsg = "No Full Data Found";
					}
				} else {
					resMsg = "No Data Found.";
				}
				logger.info("resMsg={}", resMsg);
				return resMsg;
			}
		};
	}
}