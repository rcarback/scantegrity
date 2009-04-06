<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/WEB-INF/layout/default.jsp">
    <stripes:layout-component name="contents">
    <h1>Contact Us</h1>

		<p>Please fill out the following form, and we will get back to you
		as soon as possible.</p>

		<form action="contact.php" method="get" class="stylishform">
		<fieldset><legend>Contact Form</legend>
		<ol>
			<li><label for="name">Name</label> <input type="text" id="name"
				name="name" style="width: 50%;" /></li>

			<li><label for="emailaddress">E-mail Address</label> <input
				type="text" id="emailaddress" name="emailaddress"
				style="width: 50%;" /></li>

			<li><label for="subject">Subject</label> <input type="text"
				id="subject" name="subject" style="width: 70%;" /></li>

			<li><label for="message">Message</label><br />

			<textarea id="message" name="message" cols="65" rows="20"></textarea>

			</li>
			<li>RECAPTCHA GOES HERE</li>
			<li><input type="submit" id="submit" name="submit"
				value="Send Message" /></li>
		</ol>
		</fieldset>
		</form>


	<br />
	<br />
	<br />
	<br />
	<br />
	</stripes:layout-component>
</stripes:layout-render>