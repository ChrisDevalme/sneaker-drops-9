package com.pluralsight.sneakerdrops;

import com.pluralsight.sneakerdrops.data.BrandRepository;
import com.pluralsight.sneakerdrops.data.SneakerRepository;
import com.pluralsight.sneakerdrops.models.Brand;
import com.pluralsight.sneakerdrops.models.Sneaker;
import com.pluralsight.sneakerdrops.service.DropService;
import com.pluralsight.sneakerdrops.service.SneakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class StartupRunner implements CommandLineRunner {

    private final SneakerService sneakerService;

    @Autowired
    public StartupRunner(SneakerService sneakerService) {
        this.sneakerService = sneakerService;
    }

    @Override
    public void run(String... args) throws Exception {
        sneakerService.seedIfEmpty();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Sneaker Library ---");
            System.out.println("1) List all sneakers");
            System.out.println("2) Find By Release Year");
            System.out.println("3) Find By model");
            System.out.println("4) Find By Price");
            System.out.println("5) Search");
            System.out.println("6) Find by Id");
            System.out.println("7) Add sneaker");
            System.out.println("8) Update sneaker");
            System.out.println("9) Delete sneaker");
            System.out.println("10) Search by brand");
            System.out.println("0) Quit");
            System.out.print("Your Choice: ");

            switch (scanner.nextInt()) {
                case 1 ->listSneakers();
                case 2 ->findByYear(scanner);
                case 3 ->findByModel(scanner);
                case 4 ->findByPrice(scanner);
                case 5 -> findBySearch(scanner);
                case 6 -> viewById(scanner);
                case 7 -> addSneaker(scanner);
                case 8 -> updateSneakerPrice(scanner);
                case 9 -> deleteSneaker(scanner);
                case 10 -> searchByBrand(scanner);
                case 0 -> running = false;
                default -> System.out.println("Wrong Input!");
            }
        }
    }

    private void listSneakers() {
        System.out.println("---" +  sneakerService.count() + " Sneakers---");
        for (Sneaker s : sneakerService.allSneakers()) {
            System.out.println(s.getId() + " - " + s.getModel() + "(" + s.getPrice() + ")");
        }
    }

    private void findByYear(Scanner scanner) {
        System.out.print("Year: ");
        int year = scanner.nextInt();
        for (Sneaker s : sneakerService.byYear(year)) {
            System.out.println(s.getModel() + " (" + s.getReleaseYear() + ")");
        }
    }
    private void findByModel(Scanner scanner) {
        scanner.nextLine(); // clear the leftover newline
        System.out.print("Model contains: ");
        String text = scanner.nextLine();
        for (Sneaker s : sneakerService.byModel(text)) {
            System.out.println(s.getModel());
        }
    }
    private void findByPrice(Scanner scanner) {
        System.out.print("Minimum Price: ");
        double min = scanner.nextDouble();
        for (Sneaker s : sneakerService.byPrice(min)) {
            System.out.println(s.getModel() + " (" + s.getPrice() + ")");
        }
    }

    private void findBySearch(Scanner scanner){
        System.out.print("Enter your max price range: ");
        double price = scanner.nextDouble();

        System.out.print("Enter your min year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        for (Sneaker sneaker : sneakerService.search(price, year)) {
            System.out.printf("%s ($%.2f %d)%n", sneaker.getModel(), sneaker.getPrice(), sneaker.getReleaseYear());
        }

    }

    private void viewById(Scanner scanner){
        System.out.print("Enter Sneaker id: ");
        long id = scanner.nextLong();

        Sneaker sneaker = sneakerService.byId(id);

        if (sneaker == null) {
            System.out.println("No sneaker found with that id.");
        } else {
            System.out.printf("%d - %s - $%.2f - %s ", sneaker.getId(), sneaker.getModel(), sneaker.getPrice(), sneaker.getBrand());
        }
    }

    private void listBrand() {
        for (Sneaker brand : sneakerService.allSneakers()) {
            System.out.println(brand.getId() + " - " + brand.getBrand().getName());
        }
    }

    private void addSneaker(Scanner scanner) {
        scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        System.out.print("Year: ");
        int year = scanner.nextInt();
        System.out.println("select a brand: ");
        listBrand();
        long brandId = scanner.nextLong();
        sneakerService.addSneaker(model, price, year, brandId);
        System.out.println("Added Sneaker!");
    }

    private void updateSneakerPrice(Scanner scanner) {
        System.out.print("Sneaker id: ");
        long id = scanner.nextLong();
        System.out.print("New price: ");
        double newPrice = scanner.nextDouble();
        sneakerService.updatePrice(id, newPrice);
        System.out.println("Price Updated. ");
    }

    private void deleteSneaker(Scanner scanner) {
        System.out.print("Sneaker id: ");
        long id = scanner.nextLong();
        sneakerService.deleteSneaker(id);
        System.out.println("Sneaker Deleted. ");
    }

    private void searchByBrand(Scanner scanner) {
        scanner.nextLine();
        System.out.print("Brand: ");
        String brand = scanner.nextLine();
        for (Sneaker sneaker : sneakerService.byBrand(brand)) {
            System.out.println(sneaker.getModel());
        }
    }


}