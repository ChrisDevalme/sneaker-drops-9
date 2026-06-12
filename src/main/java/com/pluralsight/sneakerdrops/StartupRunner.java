package com.pluralsight.sneakerdrops;

import com.pluralsight.sneakerdrops.data.BrandRepository;
import com.pluralsight.sneakerdrops.data.SneakerRepository;
import com.pluralsight.sneakerdrops.models.Brand;
import com.pluralsight.sneakerdrops.models.Sneaker;
import com.pluralsight.sneakerdrops.service.DropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class StartupRunner implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final SneakerRepository sneakerRepository;
    private final DropService dropService;

    @Autowired
    public StartupRunner(BrandRepository brandRepository, SneakerRepository sneakerRepository, DropService dropService) {
        this.brandRepository = brandRepository;
        this.sneakerRepository = sneakerRepository;
        this.dropService = dropService;
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();
        System.out.println(dropService.getStatus());
        for (Brand brand : brandRepository.findAll()) {
            System.out.println(brand.getId() + " - " + brand.getName());
        }
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
        System.out.println("---" +  sneakerRepository.count() + " Sneakers---");
        for (Sneaker s : sneakerRepository.findAll()) {
            System.out.println(s.getId() + " - " + s.getModel() + "(" + s.getPrice() + ")");
        }
    }

    private void findByYear(Scanner scanner) {
        System.out.print("Year: ");
        int year = scanner.nextInt();
        for (Sneaker s : sneakerRepository.findByReleaseYear(year)) {
            System.out.println(s.getModel() + " (" + s.getReleaseYear() + ")");
        }
    }
    private void findByModel(Scanner scanner) {
        scanner.nextLine(); // clear the leftover newline
        System.out.print("Model contains: ");
        String text = scanner.nextLine();
        for (Sneaker s : sneakerRepository.findByModelContaining(text)) {
            System.out.println(s.getModel());
        }
    }
    private void findByPrice(Scanner scanner) {
        System.out.print("Minimum Price: ");
        double min = scanner.nextDouble();
        for (Sneaker s : sneakerRepository.findByPriceLessThan(min)) {
            System.out.println(s.getModel() + " (" + s.getPrice() + ")");
        }
    }

    private void findBySearch(Scanner scanner){
        System.out.print("Enter your max price range: ");
        double price = scanner.nextDouble();

        System.out.print("Enter your min year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        for (Sneaker sneaker : sneakerRepository.search(price, year)) {
            System.out.printf("%s ($%.2f %d)%n", sneaker.getModel(), sneaker.getPrice(), sneaker.getReleaseYear());
        }

    }

    private void viewById(Scanner scanner){
        System.out.print("Enter Sneaker id: ");
        long id = scanner.nextLong();

        Sneaker sneaker = sneakerRepository.findById(id).orElse(null);

        if (sneaker == null) {
            System.out.println("No sneaker found with that id.");
        } else {
            System.out.printf("%d - %s - $%.2f - %s ", sneaker.getId(), sneaker.getModel(), sneaker.getPrice(), sneaker.getBrand());
        }
    }

    private void listBrand() {
        for (Brand brand : brandRepository.findAll()) {
            System.out.println(brand.getId() + " - " + brand.getName());
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
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new RuntimeException("Can't find brand with ID: " + brandId));
        sneakerRepository.save(new Sneaker(model, price, year, brand));
        System.out.println("Added Sneaker!");
    }

    private void updateSneakerPrice(Scanner scanner) {
        System.out.print("Sneaker id: ");
        long id = scanner.nextLong();

        Sneaker sneaker = sneakerRepository.findById(id).orElseThrow(() -> new RuntimeException("No sneaker with id " + id));
        System.out.print("New price: ");
        sneaker.setPrice(scanner.nextDouble());
        sneakerRepository.save(sneaker);
        System.out.println("Price Updated. ");
    }

    private void deleteSneaker(Scanner scanner) {
        System.out.print("Sneaker id: ");
        long id = scanner.nextLong();
        if (sneakerRepository.existsById(id)) {
            sneakerRepository.deleteById(id);
            System.out.println("Sneaker Deleted. ");
        } else {
            System.out.println("No sneaker with that id.");
        }
    }

    private void searchByBrand(Scanner scanner) {
        scanner.nextLine();
        System.out.print("Brand: ");
        String brand = scanner.nextLine();

        for (Sneaker sneaker : sneakerRepository.findByBrandNameContainingIgnoreCase(brand)) {
            System.out.println(sneaker.getModel());
        }
    }

    private void seedData() {
        if (sneakerRepository.count() > 0) {
            return;
        }
        Brand nike = brandRepository.save(new Brand("Nike"));
        Brand adidas = brandRepository.save(new Brand("Adidas"));
        Brand newBalance = brandRepository.save(new Brand("New Balance"));
        Brand reebok = brandRepository.save(new Brand("Reebok"));
        Brand prada = brandRepository.save(new Brand("Prada"));

        sneakerRepository.save(new Sneaker("Air Force 1", 100, 1972, nike));
        sneakerRepository.save(new Sneaker("Air Jordan 1", 180, 1973, nike));
        sneakerRepository.save(new Sneaker("Prada Cup", 900, 2000, prada));
        sneakerRepository.save(new Sneaker("Yeezy", 220, 2012, adidas));
        sneakerRepository.save(new Sneaker("Air Max", 180, 1985, nike));
        sneakerRepository.save(new Sneaker("9060", 180, 1985, newBalance));
        sneakerRepository.save(new Sneaker("Questions", 180, 1985, reebok));

    }
}