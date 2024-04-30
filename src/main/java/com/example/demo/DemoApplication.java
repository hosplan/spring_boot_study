package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
//애플리케이션을 실행 시 스프링 부트 애플리케이션 환경을 확인하고
//애플리케이션을 설정한 다음, '초기 컨텍스트'를 생성하고 스프링 부트 애플리케이션을 실행한다.
//최상위 어노테이션 @SpringBootApplication 과 한줄의 코드로 실행한다.

class Coffee{
	private final String id;
	private String name;

	public Coffee(){
		this(UUID.randomUUID().toString(), "Coffee Random");
	}
	public Coffee(String id, String name){
		this.id = id;
		this.name = name;
	}

	//Coffee 인스턴스 생성시 id 매개변수를 입력하지 않으면 고유 식별자인 id 값을 기본으로 제공
	public Coffee(String name){
		this(UUID.randomUUID().toString(), name);
	}

	public String getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}
}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController{
	private List<Coffee> coffees = new ArrayList<>();

	public RestApiDemoController(){
		coffees.addAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}

	@GetMapping
	Iterable<Coffee> getCoffees(){
		return coffees;
	}

	@GetMapping("/{id}")
	Optional<Coffee> getCoffees(@PathVariable("id") String id){
		System.out.println("id = " + id);
		for(Coffee c: coffees){
			if(c.getId().equals(id)){
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	//여기서 신기한건 Coffee 객체는 스프링 부트에 의해 언마샬링(기본값은 JSON)되어 요청한 애플리케이션이나 서비스로 반환
	//상태 코드 사용을 권장
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee){
		coffees.add(coffee);
		return coffee;
	}
	// * 마샬링 : 객체나 특정 형태의 데이터를 저장 및 전송 가능한 데이터 형태로 변환하는 과정
	// * 언마샬링 : 변환했던 데이터를 원래대로 복구하는 과정


	//PUT 요청은 파악된 URI를 통해 기존 리소스의 업데이트에 사용된다.
	//상태 코드는 필수!
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable("id") String id, @RequestBody Coffee coffee){
		int coffeeIndex = -1;

		for(Coffee c: coffees){
			if(c.getId().equals(id)){
				coffeeIndex = coffees.indexOf(c);
				coffees.set(coffeeIndex, coffee);
			}
		}

		return (coffeeIndex == -1) ?
				new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED) :
				new ResponseEntity<>(coffee, HttpStatus.OK);
	}


	//상태 코드 사용을 권장
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id){
		//removeIf는 Predicate 값을 받는다. 즉 목록에 제거할 커피가 존재시 참
		coffees.removeIf(c -> c.getId().equals(id));
	}
}
