package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.User

interface PictureService {
    fun deleteById(pictureId: Long)
    fun deleteByIds(pictureIds: List<Long>)
    fun savePicture(pictureDto: PictureDto, user: User): Picture
}