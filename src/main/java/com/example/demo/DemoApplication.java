package com.example.demo;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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


interface CoffeeRepository extends CrudRepository<Coffee, String> {}


//JPA를 사용해 DB에 데이터를 생성할 때는 기본 생성자no-argument가 필요하다.
//기본 생성자를 사용하려면 모든 멤버 변수를 final이 아닌 변경 가능으로 만들어야 한다.
@Entity
@Getter
@Setter
class Coffee{
	@Id
	private String id;
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

}

//스프링 프레임워크 4.3 이전 버전에서는 매개변수가 autowire/주입될 스프링 빈일 때 해당 메서드에 @Autowired 어노테이션을 표시
//4.3 이후 버전 부터 단일 생성자 클래스는 autowire되는 매개변수를 나타내기 위한 어노테이션이 필요 없다.
@RestController
@RequestMapping("/coffees")
class RestApiDemoController{
	private final CoffeeRepository coffeeRepository;

	public RestApiDemoController(CoffeeRepository coffeeRepository){
        this.coffeeRepository = coffeeRepository;
        this.coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));

		//coffees.addAll();
	}

	@GetMapping
	Iterable<Coffee> getCoffees(){
		return coffeeRepository.findAll();
	}

	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable("id") String id){
		return coffeeRepository.findById(id);
	}

	//여기서 신기한건 Coffee 객체는 스프링 부트에 의해 언마샬링(기본값은 JSON)되어 요청한 애플리케이션이나 서비스로 반환
	//상태 코드 사용을 권장
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee){
		return coffeeRepository.save(coffee);
	}
	// * 마샬링 : 객체나 특정 형태의 데이터를 저장 및 전송 가능한 데이터 형태로 변환하는 과정
	// * 언마샬링 : 변환했던 데이터를 원래대로 복구하는 과정


	//PUT 요청은 파악된 URI를 통해 기존 리소스의 업데이트에 사용된다.
	//상태 코드는 필수!
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable("id") String id, @RequestBody Coffee coffee){
		return (coffeeRepository.existsById(id))
				? new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.OK)
				: new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.CREATED);
	}


	//상태 코드 사용을 권장
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id){
		coffeeRepository.deleteById(id);
	}
}

@Component
class DataLoader{
	private final CoffeeRepository coffeeRepository;
	public DataLoader(CoffeeRepository coffeeRepository){
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData(){
		coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}
}