package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Tcas;
import com.railbit.tcasanalysis.repository.TcasRepo;
import com.railbit.tcasanalysis.service.TcasService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TcasServiceImpl implements TcasService {
    private final TcasRepo tcasRepo;

    @Override
    public Tcas getTcasByName(String name) {
        System.out.println(name);
        System.out.println(tcasRepo.findByName(name));
        return tcasRepo.findByName(name);
    }

    @Override
    public Tcas getTcasById(Integer id) {
        Optional<Tcas> data=tcasRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Tcas not found");
        return data.get();
    }

    @Override
    public List<Tcas> getAllTcas() {
        return tcasRepo.findAll();
    }

    @Override
    public int postTcas(Tcas tcas) {
       Tcas newTcas = tcasRepo.save(tcas);
       return newTcas.getId();
    }

    @Override
    public void updateTcas(Tcas tcas) {
        tcasRepo.save(tcas);
    }

    @Override
    public void deleteTcasById(Integer id) {
        tcasRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
