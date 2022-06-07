package apiChaining;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndTest {

	Response response;
	String BaseURI = "http://3.91.99.123:8088/employees";

	@Test
	public void test1() {

		response = GetMethodAll();
		Assert.assertEquals(response.getStatusCode(), 200);
		System.out.println(response.getBody().asString());

		response = PostMethod("FN", "LN", "5000", "fnln@abc.com");
		Assert.assertEquals(response.getStatusCode(), 201);
		JsonPath jspath = response.jsonPath();
		int EMP_ID = jspath.get("id");
		System.out.println("New EMP ID is: " + EMP_ID);

		response = PutMethod(EMP_ID, "FN-Updated", "LN-Updated", "15000", "fnln-updated@abc.com");
		Assert.assertEquals(response.getStatusCode(), 200);
		jspath = response.jsonPath();
		Assert.assertEquals(jspath.get("firstName"), "FN-Updated");
		String Name = jspath.get("firstName");
		System.out.println("Updated Name is: " + Name);

		response = DeleteMethod(EMP_ID);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().asString(), "");

		response = GetMethod(EMP_ID);
		Assert.assertEquals(response.getStatusCode(), 400);
		jspath = response.jsonPath();
		Assert.assertEquals(jspath.get("message"), "Entity Not Found");
		System.out.println("Employee " + EMP_ID + " is deleted.");

		response = GetMethodAll();
		Assert.assertEquals(response.getStatusCode(), 200);
		System.out.println(response.getBody().asString());

	}

	public Response GetMethodAll() {
		RestAssured.baseURI = BaseURI;
		RequestSpecification request = RestAssured.given();
		Response response = request.get();
		return response;
	}

	public Response PostMethod(String firstName, String lastName, String Salary, String email) {
				
		Map<String, Object> MapObj = new HashMap<String, Object>();
		MapObj.put("firstName", firstName);
		MapObj.put("lastName", lastName);
		MapObj.put("salary", Salary);
		MapObj.put("email", email);

		RestAssured.baseURI = BaseURI;
		RequestSpecification request = RestAssured.given();
		Response response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj).post();
		return response;
	}

	public Response PutMethod(int EMP_ID, String firstName, String lastName, String Salary, String email) {

		JSONObject jobj = new JSONObject();
		jobj.put("firstName", firstName);
		jobj.put("lastName", lastName);
		jobj.put("salary", Salary);
		jobj.put("email", email);

		RestAssured.baseURI = BaseURI;
		RequestSpecification request = RestAssured.given();
		Response response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(jobj.toString())
				.put("/" + EMP_ID);
		return response;
	}

	public Response GetMethod(int EMP_ID) {
		RestAssured.baseURI = BaseURI;
		RequestSpecification request = RestAssured.given();
		Response response = request.get("/" + EMP_ID);
		return response;
	}

	public Response DeleteMethod(int EMP_ID) {

		RestAssured.baseURI = BaseURI;
		RequestSpecification request = RestAssured.given();
		Response response = request.delete("/" + EMP_ID);
		return response;
	}

}
