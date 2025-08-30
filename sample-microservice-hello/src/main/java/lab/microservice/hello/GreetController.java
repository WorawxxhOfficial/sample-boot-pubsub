package lab.microservice.hello;

import org.apache.kafka.common.protocol.types.Field.Str;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetController {

	private final KafkaTemplate<String, String> kafkaTemplate;
	@Value("${app.kafka.topic:greetings}")
	private String topicName;

	public GreetController(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	// basic hello 
	@GetMapping("/hello/{name}")
	public ResponseEntity<String> hello(@PathVariable String name){
		String message = "Hello " + name;
		kafkaTemplate.send(topicName, message);
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

}