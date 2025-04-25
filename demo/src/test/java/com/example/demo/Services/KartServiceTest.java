package com.example.demo.Services;

import com.example.demo.Entities.KartEntity;
import com.example.demo.Repositories.KartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class KartServiceTest {
    @Mock
    private KartRepository kartRepository;

    @InjectMocks
    private KartService kartService;

    @Test
    void testSaveKart() {
        KartEntity kart = new KartEntity();
        kart.setId(1L);
        kart.setAvailable(true);

        when(kartRepository.save(kart)).thenReturn(kart);

        KartEntity result = kartService.saveKart(kart);
        assertThat(result).isEqualTo(kart);
    }

    @Test
    void testGetKartsByAvailability() {
        KartEntity kart1 = new KartEntity();
        kart1.setAvailable(true);
        KartEntity kart2 = new KartEntity();
        kart2.setAvailable(true);

        when(kartRepository.findByAvailable(true)).thenReturn(List.of(kart1, kart2));

        List<KartEntity> result = kartService.getKartsByAvailability(true);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(KartEntity::isAvailable);
    }

    @Test
    void testUpdateKart() {
        KartEntity kart = new KartEntity();
        kart.setId(2L);
        kart.setAvailable(false);

        when(kartRepository.save(kart)).thenReturn(kart);

        KartEntity result = kartService.updateKart(kart);
        assertThat(result).isEqualTo(kart);
    }

    @Test
    void testDeleteKartSuccess() throws Exception {
        doNothing().when(kartRepository).deleteById(1L);

        boolean result = kartService.deleteKart(1L);

        assertTrue(result);
        verify(kartRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteKartThrowsException() {
        doThrow(new RuntimeException("Deletion failed")).when(kartRepository).deleteById(99L);

        Exception ex = assertThrows(Exception.class, () -> kartService.deleteKart(99L));
        assertThat(ex.getMessage()).contains("Deletion failed");
    }
}
