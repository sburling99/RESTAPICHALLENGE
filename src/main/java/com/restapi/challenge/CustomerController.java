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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EnableAutoConfiguration
@RestController
@Controller
@ResponseBody
class CustomerController {

	public static final String insertCustomerPath = "insertCustomer.sql";
	public static final String listAllCustomerPath = "selectAll.sql";
	public static final String listByCityPath = "selectByCity.sql";
	public static final String listByIdPath = "selectById.sql";

	public String loadSQL(String filename) throws IOException {
		InputStream fileStream = CustomerController.class.getClassLoader().getResourceAsStream(filename);
		assert fileStream != null;
		return new String(fileStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=CustomerDB;integratedSecurity=true";

		return DriverManager.getConnection(connectionUrl);
	}

	String listByCity(String city){
		List<JSONObject> jsonObjects = new ArrayList<>();
		try (Connection con = getConnection()) {

			PreparedStatement stmt = con.prepareStatement(loadSQL(listByCityPath));

			stmt.setString(1, city);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject newObject = new JSONObject();

				newObject.put("CUSTOMER ID", rs.getInt("CustomerId"));
				newObject.put("FIRST NAME", rs.getString("FirstName"));
				newObject.put("LAST NAME", rs.getString("LastName"));
				newObject.put("ADDRESS", rs.getString("Address"));
				newObject.put("CITY", rs.getString("City"));
				newObject.put("STATE", rs.getString("State"));
				newObject.put("ZIP", rs.getInt("Zip"));

				jsonObjects.add(newObject);
			}
		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return jsonObjects.toString();
	}

	String listAll(){
		List<JSONObject> jsonObjects = new ArrayList<>();
		try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
			ResultSet rs = stmt.executeQuery(loadSQL(listAllCustomerPath));
			while (rs.next()) {
				JSONObject newObject = new JSONObject();

				newObject.put("CUSTOMER ID", rs.getInt("CustomerId"));
				newObject.put("FIRST NAME", rs.getString("FirstName"));
				newObject.put("LAST NAME", rs.getString("LastName"));
				newObject.put("ADDRESS", rs.getString("Address"));
				newObject.put("CITY", rs.getString("City"));
				newObject.put("STATE", rs.getString("State"));
				newObject.put("ZIP", rs.getInt("Zip"));

				jsonObjects.add(newObject);
			}
		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return jsonObjects.toString();
	}

	@RequestMapping(value = "/customers", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	String newCustomer(@RequestBody Map<String, Object> inputData) throws SQLException, ClassNotFoundException {
		JSONObject jsonObject = new JSONObject(inputData);
		try (Connection con = getConnection()){
			PreparedStatement stmt = con.prepareStatement(loadSQL(insertCustomerPath));

			stmt.setString(1, inputData.getOrDefault("FIRST NAME", "N/A").toString());
			stmt.setString(2, inputData.getOrDefault("LAST NAME", "N/A").toString());
			stmt.setString(3, inputData.getOrDefault("EMAIL", "N/A").toString());
			stmt.setString(4, inputData.getOrDefault("ADDRESS", "N/A").toString());
			stmt.setString(5, inputData.getOrDefault("CITY", "N/A").toString());
			stmt.setString(6, inputData.getOrDefault("STATE", "N/A").toString());
			stmt.setString(7, inputData.getOrDefault("ZIP", "N/A").toString());

			stmt.executeUpdate();

			con.commit();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		return inputData.get("FIRST NAME") + " was added to database.";
	}

	@RequestMapping("/customers/{id}")
	@ResponseBody
	ResponseEntity<Object> listId(@PathVariable Long id) {

		List<JSONObject> jsonObjects = new ArrayList<>();

		try (Connection con = getConnection()) {

			PreparedStatement stmt = con.prepareStatement(loadSQL(listByIdPath));

			stmt.setInt(1, Math.toIntExact(id));

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject newObject = new JSONObject();

				newObject.put("CUSTOMER ID", rs.getInt("CustomerId"));
				newObject.put("FIRST NAME", rs.getString("FirstName"));
				newObject.put("LAST NAME", rs.getString("LastName"));
				newObject.put("ADDRESS", rs.getString("Address"));
				newObject.put("CITY", rs.getString("City"));
				newObject.put("STATE", rs.getString("State"));
				newObject.put("ZIP", rs.getInt("Zip"));

				jsonObjects.add(newObject);
			}
		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(jsonObjects.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<Object> listCity(@RequestParam(required = false) String city) {
		if (city == null){
			return new ResponseEntity<>(listAll(), HttpStatus.OK);
		}else{
			return new ResponseEntity<>(listByCity(city), HttpStatus.OK);
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
//	@DeleteMapping("/customers/{id}")
//	void deleteCustomer(@PathVariable Long id) {
//		repository.deleteById(id);
//	}
}