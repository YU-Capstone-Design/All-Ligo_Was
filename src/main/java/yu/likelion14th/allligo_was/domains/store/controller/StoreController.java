package yu.likelion14th.allligo_was.domains.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.store.dto.response.PublicStoreResDto;
import yu.likelion14th.allligo_was.domains.store.service.StoreService;
import yu.likelion14th.allligo_was.domains.store.api.StoreAPI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController implements StoreAPI {

    private final StoreService storeService;

    @Override
    @GetMapping("/public")
    public ResponseEntity<List<PublicStoreResDto>> getPublicStores() {
        return ResponseEntity.ok(storeService.getPublicStores());
    }
}
