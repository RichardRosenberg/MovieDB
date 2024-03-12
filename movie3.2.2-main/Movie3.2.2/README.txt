Step One: create the schema (empty) for our db. Must be called moviedb the connection details
	  are in application.properties

Step Two: Run the program and register an admin profile. You can do this by simply
          signing up its defaulted to admin.

Step Three: Stop the program and run this code in the database on mySQL.
	    INSERT INTO roles(id, name) VALUES (2,'ROLE_USER');

Step Four: Go to UserServicesImpl and change the two lines of code marked with comments.
	   The lines can be found by searching for ROLE.

Step Five: Now any new user will have a USER role not ADMIN. The program should now function.

Left to do: 
		-validation on login, signup and movie form. 
		-Create a user reviews page and review form.
		-Add route for the user reviews and a button next to each movie to add review.
		-Add security so only USER can leave review not unlogged users.