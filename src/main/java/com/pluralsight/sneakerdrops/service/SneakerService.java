package com.pluralsight.sneakerdrops.service;

import com.pluralsight.sneakerdrops.data.BrandRepository;
import com.pluralsight.sneakerdrops.data.SneakerRepository;
import com.pluralsight.sneakerdrops.models.Brand;
import com.pluralsight.sneakerdrops.models.Sneaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SneakerService {

    private final BrandRepository brandRepository;
    private final SneakerRepository sneakerRepository;

    @Autowired
    public SneakerService(BrandRepository brandRepository, SneakerRepository sneakerRepository, DropService dropService) {
        this.brandRepository = brandRepository;
        this.sneakerRepository = sneakerRepository;
    }

    public long count() {
        return sneakerRepository.count();
    }

    public List<Sneaker> allSneakers() {
        return sneakerRepository.findAll();
    }

    public List<Brand> allBrands() {
        return brandRepository.findAll();
    }

    public List<Sneaker> byYear(int year) {
        return sneakerRepository.findByReleaseYear(year);
    }

    public List<Sneaker> byModel(String model) {
        return sneakerRepository.findByModelContaining(model);
    }

    public List<Sneaker> byPrice(double price) {
        return sneakerRepository.findByPriceLessThan(price);
    }

    public List<Sneaker> search(double price, int year) {
        return sneakerRepository.search(price, year);
    }

    public List<Sneaker> byBrand(String brandName){
        return sneakerRepository.findByBrandNameContainingIgnoreCase(brandName);
    }

    public Sneaker byId(long id) {
        return sneakerRepository.findById(id).orElseThrow(() -> new NotFoundException("No sneaker with id " + id));
    }

    public Sneaker addSneaker(String model, double price , int year, long brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new NotFoundException("No brand with id " + brandId));
        return sneakerRepository.save(new Sneaker(model, price, year, brand));
    }

    public Sneaker updatePrice(long id, double price) {
        Sneaker sneaker = byId(id);
        sneaker.setPrice(price);
        return sneakerRepository.save(sneaker);
    }

    public void deleteSneaker(long id) {
        if (!sneakerRepository.existsById(id)) {
            throw new NotFoundException("No sneaker found by id " + id);
        }

        sneakerRepository.deleteById(id);
    }

    public void seedIfEmpty() {
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
