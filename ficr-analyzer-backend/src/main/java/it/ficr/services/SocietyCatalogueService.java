package it.ficr.services;

import com.fasterxml.jackson.databind.JsonNode;
import it.ficr.elements.Society;
import it.ficr.repositories.SocietyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SocietyCatalogueService {

    @Autowired
    private SocietyRepository societyRepository;


    public Society updateSociety(Society soc){
        societyRepository.saveAndFlush(soc);
        return soc;
    }
    public Society buildSociety(String name, String code){
        Optional<Society> soc = societyRepository.findByName(name);
        if(soc.isPresent()){
            return soc.get();
        }else{
            Society nSoc = new Society(name, code, null);
            societyRepository.saveAndFlush(nSoc);
            return nSoc;
        }
    }

}
