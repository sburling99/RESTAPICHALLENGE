package com.restapi.challenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=CustomerDB;integratedSecurity=true";

		return DriverManager.getConnection(connectionUrl);
	}

//	@PostMapping("/customers")
//	String newCustomer(@RequestBody Customer newCustomer) throws ClassNotFoundException {
//
//		return newCustomer.getName() + " " + newCustomer.getRole();
//	}

	String ListByCity(String city){
		List<JSONObject> jsonObjects = new ArrayList<>();
		try (Connection con = getConnection()) {

			//String SQL = "SELECT * FROM dbo.Customers WHERE City = ?";
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM dbo.Customers WHERE City = ?");

			stmt.setString(1, city);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject newObject = new JSONObject();

				newObject.put("Customer ID", rs.getInt("CustomerId"));
				newObject.put("First name", rs.getString("FirstName"));
				newObject.put("Last name", rs.getString("LastName"));
				newObject.put("City", rs.getString("City"));

				jsonObjects.add(newObject);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return jsonObjects.toString();
	}

	String ListAll(){
		List<JSONObject> jsonObjects = new ArrayList<>();
		try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
			String SQL = "SELECT * FROM dbo.Customers";

			ResultSet rs = stmt.executeQuery(SQL);
			int i = 0;
			while (rs.next()) {
				JSONObject newObject = new JSONObject();
				i++;

				newObject.put("Customer ID", rs.getInt("CustomerId"));
				newObject.put("First name", rs.getString("FirstName"));
				newObject.put("Last name", rs.getString("LastName"));
				newObject.put("City", rs.getString("City"));

				jsonObjects.add(newObject);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return jsonObjects.toString();
	}
	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<Object> listCity(@RequestParam(required = false) String city) {
		if (city == null){
			return new ResponseEntity<>(ListAll(), HttpStatus.OK);
		}else{
			return new ResponseEntity<>(ListByCity(city), HttpStatus.OK);
		}
	}
//
//	@RequestMapping(value = "/customers")
//	@ResponseBody
//	ResponseEntity<Object> listAll() {
//		List<JSONObject> jsonObjects = new ArrayList<>();
//		try (Connection con = getConnection(); Statement stmt = con.createStatement();) {
//			String SQL = "SELECT * FROM dbo.Customers";
//
//			ResultSet rs = stmt.executeQuery(SQL);
//			int i = 0;
//			while (rs.next()) {
//				JSONObject newObject = new JSONObject();
//				i++;
//
//				newObject.put("Customer ID", i);
//				newObject.put("First name", rs.getString("FirstName"));
//				newObject.put("Last name", rs.getString("LastName"));
//				newObject.put("Role", "Baller");
//
//				jsonObjects.add(newObject);
//				System.out.println(rs.getString("FirstName") + " " + rs.getString("LastName"));
//			}
//		} catch (SQLException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		return new ResponseEntity<>(jsonObjects.toString(), HttpStatus.OK);
//	}

//	@PutMapping("/customers/{id}")
//	String replaceCustomer(@RequestBody Customer newCustomer, @PathVariable Long id) {
//		return "Testing";
//	}

//	@DeleteMapping("/customers/{id}")
//	void deleteCustomer(@PathVariable Long id) {
//		repository.deleteById(id);
//	}
}