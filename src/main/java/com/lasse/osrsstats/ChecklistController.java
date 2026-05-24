package com.lasse.osrsstats;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checklist")
public class ChecklistController {

    private final ChecklistRepository repository;

    public ChecklistController(ChecklistRepository repository) {
        this.repository = repository;
    }

    // GET /api/checklist/LaksenGI → spillerens liste i fast rekkefølge.
    // Rekkefølgen her bestemmer hvilket nummer hver oppgave har (1, 2, 3...).
    @GetMapping("/{username}")
    public List<ChecklistItem> getForPlayer(@PathVariable String username) {
        return repository.findByUsernameOrderByCreatedAtAsc(username);
    }

    // POST /api/checklist/LaksenGI/add  med body {"task": "level 62 construction"}
    // Fritekst – alt som sendes inn blir oppgaveteksten.
    @PostMapping("/{username}/add")
    public ChecklistItem add(@PathVariable String username,
                             @RequestBody Map<String, String> body) {
        String task = body.get("task");
        return repository.save(new ChecklistItem(username, task));
    }

    // POST /api/checklist/LaksenGI/complete/1 → markerer oppgave nr. 1 som fullført.
    // Oppgaven blir IKKE slettet – den står igjen i lista, bare markert ferdig.
    @PostMapping("/{username}/complete/{number}")
    public ChecklistItem complete(@PathVariable String username,
                                  @PathVariable int number) {
        ChecklistItem item = findByNumber(username, number);
        item.setCompleted(true);
        return repository.save(item);
    }

    // DELETE /api/checklist/LaksenGI/delete/1 → fjerner oppgave nr. 1 helt.
    @DeleteMapping("/{username}/delete/{number}")
    public void delete(@PathVariable String username,
                       @PathVariable int number) {
        ChecklistItem item = findByNumber(username, number);
        repository.delete(item);
    }

    // Hjelpemetode: finner oppgave nr. N (1-basert) i spillerens liste.
    // Nummeret følger samme rekkefølge som GET-endepunktet returnerer.
    private ChecklistItem findByNumber(String username, int number) {
        List<ChecklistItem> items =
                repository.findByUsernameOrderByCreatedAtAsc(username);

        // Nummer er 1-basert for brukeren, men lista er 0-basert i Java
        int index = number - 1;
        if (index < 0 || index >= items.size()) {
            throw new RuntimeException(
                    "Fant ingen oppgave nr. " + number + " for " + username);
        }
        return items.get(index);
    }
}