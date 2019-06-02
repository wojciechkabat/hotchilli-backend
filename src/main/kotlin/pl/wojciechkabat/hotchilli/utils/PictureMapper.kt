package pl.wojciechkabat.hotchilli.utils

import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.entities.Picture
import java.util.stream.Collectors.toList as toList

class PictureMapper()  {
    companion object PictureMapper {
        fun mapToDto(pictureEntity: Picture): PictureDto {
            return PictureDto(
                    pictureEntity.id,
                    pictureEntity.externalIdentifier,
                    pictureEntity.url
            )
        }

        fun mapToEntity(pictureDto: PictureDto): Picture {
            return Picture(
                    pictureDto.id,
                    pictureDto.externalIdentifier,
                    pictureDto.url
            )
        }

        fun mapToDto(pictureEntities: List<Picture>): List<PictureDto> {
            return pictureEntities.stream().map { mapToDto(it) }.collect(toList())
        }

        fun mapToEntity(pictureDtos: List<PictureDto>): List<Picture> {
            return pictureDtos.stream().map { mapToEntity(it) }.collect(toList())
        }
    }

}