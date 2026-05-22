package com.epam.gym.component.steps;

import com.epam.gym.dto.TrainingTypeDTO;
import com.epam.gym.entity.TrainingType;
import com.epam.gym.enums.TrainingTypeEnum;
import com.epam.gym.mapper.training_type.TrainingTypeMapperI;
import com.epam.gym.repository.TrainingTypeRepository;
import com.epam.gym.service.TrainingTypeService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

public class TrainingTypeServiceStepDefinition {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeMapperI trainingTypeMapperI;

    private TrainingTypeService trainingTypeService;

    private List<TrainingTypeDTO> result;

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        trainingTypeService =
                new TrainingTypeService(
                        trainingTypeRepository,
                        trainingTypeMapperI
                );
    }

    @Given("training types exist")
    public void training_types_exist() {

        TrainingType entity1 = new TrainingType();
        entity1.setId(1L);
            entity1.setTrainingTypeName(TrainingTypeEnum.YOGA);

        TrainingType entity2 = new TrainingType();
        entity2.setId(2L);
        entity2.setTrainingTypeName(TrainingTypeEnum.CARDIO);

        TrainingTypeDTO dto1 = new TrainingTypeDTO();
        dto1.setId(1L);
        dto1.setTrainingTypeName(TrainingTypeEnum.YOGA);

        TrainingTypeDTO dto2 = new TrainingTypeDTO();
        dto2.setId(2L);
        dto2.setTrainingTypeName(TrainingTypeEnum.CARDIO);

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        when(trainingTypeMapperI.toDTO(entity1))
                .thenReturn(dto1);

        when(trainingTypeMapperI.toDTO(entity2))
                .thenReturn(dto2);
    }

    @Given("no training types exist")
    public void no_training_types_exist() {

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of());
    }

    @When("all training types are requested")
    public void all_training_types_are_requested() {

        result = trainingTypeService.getAllTrainingTypes();
    }

    @Then("training type list should be returned")
    public void training_type_list_should_be_returned() {

        Assertions.assertNotNull(result);

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(
                TrainingTypeEnum.YOGA,
                result.get(0).getTrainingTypeName()
        );

        Assertions.assertEquals(
                TrainingTypeEnum.CARDIO,
                result.get(1).getTrainingTypeName()
        );
    }

    @Then("empty training type list should be returned")
    public void empty_training_type_list_should_be_returned() {

        Assertions.assertNotNull(result);

        Assertions.assertTrue(result.isEmpty());
    }
}
