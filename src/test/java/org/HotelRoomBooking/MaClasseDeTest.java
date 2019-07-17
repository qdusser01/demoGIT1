package org.HotelRoomBooking;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;


public class MaClasseDeTest {


	WebDriver driver;
	BddConnexion bdd_c= new BddConnexion();
		
	@Before
	public void setup() {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
	}
	@After
	public void tearDown() throws Exception {
	// DELETE LES DONNEES DE TEST --> Nettoyage de la base
		System.out.println("Nettoyage de la BDD");
		bdd_c.deleteAllData("src/main/resources/delete_reservation.xml");
		driver.quit();
	}
	
	@Test
	public void testDeSchwitchFrame() throws Exception {

		Calendar date = new GregorianCalendar();
		int dateChiffre = date.get(Calendar.DAY_OF_MONTH)*5-4;
		String dateString = Integer.toString(dateChiffre);
		System.out.println(dateString);
		
	//connexion à l'application TutorialHtml5HotelPhp
	driver.get("http://localhost/TutorialHtml5HotelPhp/");
	
	//vérification de l'affichage de la page "HTML5 Hotel Room Booking"
	assertEquals("HTML5 Hotel Room Booking (JavaScript/PHP)", driver.findElement(By.xpath("//h1")).getText());
	
	//clic sur la première cellule du planing
	WebElement cellule1 = driver.findElement(By.xpath("//div[contains(@class,'scheduler_default_cell')]["+dateString+"]"));
	String position = cellule1.getAttribute("style").substring(0, 46);
	System.out.println(" voici la position de la cellule sélectionnée par le test : "+ position);
	cellule1.click();
	
	//gestion des frame : switch sur la popup
	driver.switchTo().frame(0);
	
	//verification du titre de la pop up
	assertEquals("New Reservation", driver.findElement(By.xpath("//h1")).getText());

	//ecriture du nom de la réservation
	driver.findElement(By.id("name")).sendKeys("resa1");
	
	// sauvegarde de la reservation
	driver.findElement(By.xpath("//input[@value='Save']")).click();
	
	//gestion des frame : switch sur le contenu html par défaut
	driver.switchTo().defaultContent();
	
	//vérification de l'enregistrement de la réservation
	try {
		WebElement reservation = driver.findElement(By.xpath("//div[@class='scheduler_default_event_inner']"));
		assertTrue(reservation.getText().contains("resa1"));
		//assertTrue(reservation.findElement(By.xpath("..")).getAttribute("style").contains(position));
	}
	catch(Exception e) {
		System.out.println(" [[ECHEC]] la reservation ne semble pas avoir été enregistrée");
		throw e;
	}
	
	
	System.out.println("[[SUCCES]] la reservation a bien été enregistrée");
	
	
	bdd_c.compareData("reservations", "src/main/resources/insert_reservation.xml", "id");
	}
	
	
	
	@Test
	public void testDragAndDrop() throws Exception {
		
	// INSERT LES JDD EN BASE
	bdd_c.insertData("src/main/resources/insert_reservation.xml");
	
	//connexion à l'application TutorialHtml5HotelPhp
	driver.get("http://localhost/TutorialHtml5HotelPhp/");
	
	//vérification de l'affichage de la page "HTML5 Hotel Room Booking"
	assertEquals("HTML5 Hotel Room Booking (JavaScript/PHP)", driver.findElement(By.xpath("//h1")).getText());
	
	//effectue un dragAndDrop de la cellule1 à la cellule 2
	WebElement cellule1=driver.findElement(By.xpath("//div[@class='scheduler_default_event scheduler_default_event_line0']"));
	WebElement cellule2=driver.findElement(By.xpath("//div[@class='scheduler_default_cell'][6]"));
	
	Actions actions = new Actions(driver);
	actions.clickAndHold(cellule1).moveToElement(cellule2).release(cellule2).perform();

	Thread.sleep(700);
	
	//verifie l'appartition du message "update successfull"
	assertTrue(driver.findElement(By.xpath("//div[.='Update successful']")).isDisplayed());
	
	//attends 10 sec
	Thread.sleep(10000);
	
	//Vérifie la disparition du message "update successfull"
	assertFalse(driver.findElement(By.xpath("//div[.='Update successful']")).isDisplayed());
	
	// DELETE LES DONNEES DE TEST --> Nettoyage de la base
//	bdd_c.deleteAllData("src/main/resources/delete_reservation.xml");
	}
	
	@Test
	public void testMouseHover() throws Exception {
		
		// INSERT LES JDD EN BASE
		bdd_c= new BddConnexion();
		bdd_c.insertData("src/main/resources/insert_reservation.xml");
		
		//connexion à l'application TutorialHtml5HotelPhp
		driver.get("http://localhost/TutorialHtml5HotelPhp/");
		
		//vérification de l'affichage de la page "HTML5 Hotel Room Booking"
		assertEquals("HTML5 Hotel Room Booking (JavaScript/PHP)", driver.findElement(By.xpath("//h1")).getText());
		WebElement element=driver.findElement(By.xpath("//div[@class='scheduler_default_event scheduler_default_event_line0']"));
		
		//Opère le mouseHover sur la réservation
		Actions actions = new Actions(driver);
		actions.moveToElement(element).build().perform();
		
		//clic sur le bouton delete
		driver.findElement(By.xpath("//div[@class='scheduler_default_event_delete']")).click();

		//verifie l'appartition du message "Deleted"
		assertTrue(driver.findElement(By.xpath("//div[.='Deleted.']")).isDisplayed());
		
		//attends 10 sec
		Thread.sleep(10000);
		
		//Vérifie la disparition du message "Deleted"
		assertFalse(driver.findElement(By.xpath("//div[.='Deleted.']")).isDisplayed());
		
	}

}
