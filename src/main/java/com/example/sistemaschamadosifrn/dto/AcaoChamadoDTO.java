package com.example.sistemaschamadosifrn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AcaoChamadoDTO {
    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotBlank(message = "O texto da interação é obrigatório")
    private String texto; // Usado apenas no POST de interações
}