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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.geodetails.application.model.ResponseModel;
import com.geodetails.application.model.ResultsWrapperModel;

@Service
public class GeoRepesponseService {

	Logger logger = LoggerFactory.getLogger(GeoRepesponseService.class);

	@Value("${mockservice.url}")
	String mockserviceUrl;
	
	@Autowired 
	ResponseModel responseModel;

	public Callable<ResponseModel> getGeoDetails(String ip) throws InterruptedException {
		return new Callable<ResponseModel>() {
			@Override
			public ResponseModel call() throws Exception {
				RestTemplate restTemplate = new RestTemplate();
				String resMsg = null;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				Map<String, String> inpt = new HashMap<String, String>();
				inpt.put("ip", ip);
				logger.info(" mockserviceUrl={}  ip={}", mockserviceUrl,ip);
				ResponseEntity<ResultsWrapperModel> responseEntity = restTemplate.postForEntity(mockserviceUrl, inpt,
						ResultsWrapperModel.class);
				ResultsWrapperModel responseResultModel = responseEntity.getBody();
				logger.info("responseResultModel=" + responseResultModel.getResults());
				String addrs[] = null;
				if (responseResultModel.getResults() != null && responseResultModel.getResults().length > 0) {
					if (responseResultModel.getResults()[0].getFormattedAddressLines() != null
							&& responseResultModel.getResults()[0].getFormattedAddressLines().length > 2) {
						addrs = responseResultModel.getResults()[0].getFormattedAddressLines();
						resMsg = addrs[0] + "," + addrs[1] + "," + addrs[2];
						responseModel.setResCode(201);
						responseModel.setResMsg(resMsg);
					} else {
						responseModel.setResCode(HttpStatus.NOT_FOUND.value());
						responseModel.setResMsg("No Full Data Found");
					}
				} else {
					responseModel.setResCode(HttpStatus.NOT_FOUND.value());
					responseModel.setResMsg("No Data Found");
				}
				logger.info("resMsg={}", resMsg);
				return responseModel;
			}
		};
	}
}