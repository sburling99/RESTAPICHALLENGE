package com.restapi.challenge;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@EnableAutoConfiguration
@RestController
@Controller
@ResponseBody
class CustomerController {

	//	@GetMapping("/customers")
	//	@ResponseBody
	//	List<Customer> all() {
	//		return repository.findAll();
	//	}
//	public Connection getConnection(){
//		S
//	}

	@PostMapping("/customers")
	String newCustomer(@RequestBody Customer newCustomer) {

		return newCustomer.getName() + " " + newCustomer.getRole();
	}

	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	@ResponseBody
	String one(@RequestParam(value = "city") String city) {
		return "Testing, here's your city back: " + city;
	}

	@PutMapping("/customers/{id}")
	String replaceCustomer(@RequestBody Customer newCustomer, @PathVariable Long id) {
		return "Testing";
	}

//	@DeleteMapping("/customers/{id}")
//	void deleteCustomer(@PathVariable Long id) {
//		repository.deleteById(id);
//	}
}