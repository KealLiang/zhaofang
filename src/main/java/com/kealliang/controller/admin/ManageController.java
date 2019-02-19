package com.kealliang.controller.admin;

import com.google.gson.Gson;
import com.kealliang.base.ApiDataTableResponse;
import com.kealliang.base.ApiResponse;
import com.kealliang.base.ServiceMultiResult;
import com.kealliang.base.ServiceResult;
import com.kealliang.base.constant.HouseOperation;
import com.kealliang.base.constant.HouseStatus;
import com.kealliang.dto.*;
import com.kealliang.dto.form.DatatableSearch;
import com.kealliang.dto.form.HouseForm;
import com.kealliang.dto.form.PhotoForm;
import com.kealliang.entity.SupportAddress;
import com.kealliang.service.AddressService;
import com.kealliang.service.HouseService;
import com.kealliang.service.QiniuService;
import com.qiniu.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lsr
 * @ClassName ManageController
 * @Date 2019-01-31
 * @Desc 后台管理
 * @Vertion 1.0
 */
@Controller
@RequestMapping("admin")
public class ManageController {

    private static final Logger LOG = LoggerFactory.getLogger(ManageController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private Gson gson;

    @Value("${spring.servlet.multipart.location}")
    private String tempFilePath;

    @GetMapping("login")
    public String login(){
        return "admin/login";
    }

    @GetMapping("{page}")
    public String toPage(@PathVariable String page) {
        return "admin/" + page;
    }

    @GetMapping("house/{page}")
    public String toHousePage(@PathVariable String page) {
        return "admin/house-" + page;
    }

    @GetMapping("add/house")
    public String toHousePage() {
        return "admin/house-add";
    }

    /**
     * 房源列表页
     * @return
     */
    @GetMapping("house/list")
    public String houseListPage() {
        return "admin/house-list";
    }

    @PostMapping("houses")
    @ResponseBody
    public ApiDataTableResponse houses(@ModelAttribute DatatableSearch searchBody) {
        ServiceMultiResult<HouseDTO> result = houseService.adminQuery(searchBody);

        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        response.setData(result.getResult());
        response.setRecordsFiltered(result.getTotal());
        response.setRecordsTotal(result.getTotal());

        response.setDraw(searchBody.getDraw());
        return response;
    }

    /** 
     * 上传图片
     * @author lsr
     * @description uploadPhoto
     * @Date 16:32 2019/2/3
     * @Param [file]
     * @return com.kealliang.base.ApiResponse
     */
    @PostMapping(value = "upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        String fileName = file.getOriginalFilename();
        String path = tempFilePath + "/" + fileName;
        File targetFile = new File(path);
        try {
            file.transferTo(targetFile);
            Response response = qiniuService.upload(file.getInputStream());
            if (response.isOK()) {
                QiNiuPutRet putRet = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(putRet);
            } else {
                return ApiResponse.ofMessage(response.statusCode, response.getInfo());
            }
        } catch (IOException e) {
            LOG.error("上传文件[{}]出错", fileName);
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 新增房源接口
     * @param houseForm
     * @param bindingResult
     * @return
     */
    @PostMapping("add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }

        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
        }
        // todo 先暂时注释封面校验02
//        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
//            List<PhotoForm> list = getDefaultPhotoForms();
//            houseForm.setPhotos(list);
//            houseForm.setCover("未来都市.jpg");
//        }

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult<HouseDTO> result = houseService.save(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(result.getResult());
        }

        return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
    }

    private List<PhotoForm> getDefaultPhotoForms() {
        PhotoForm photoForm = new PhotoForm();
        photoForm.setPath("D:\\coding\\core-hr\\zhaofang\\temp\\未来都市.jpg");
        photoForm.setHeight(900);
        photoForm.setWidth(1600);
        List<PhotoForm> list = new ArrayList<>();
        list.add(photoForm);
        return list;
    }

    /**
     * 房源信息编辑页
     * @return
     */
    @GetMapping("house/edit")
    public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {

        if (id == null || id < 1) {
            return "404";
        }

        ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(id);
        if (!serviceResult.isSuccess()) {
            return "404";
        }

        HouseDTO result = serviceResult.getResult();
        model.addAttribute("house", result);

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(result.getCityEnName(), result.getRegionEnName());
        model.addAttribute("city", addressMap.get(SupportAddress.Level.CITY));
        model.addAttribute("region", addressMap.get(SupportAddress.Level.REGION));

        HouseDetailDTO detailDTO = result.getHouseDetail();
        ServiceResult<SubwayDTO> subwayServiceResult = addressService.findSubway(detailDTO.getSubwayLineId());
        if (subwayServiceResult.isSuccess()) {
            model.addAttribute("subway", subwayServiceResult.getResult());
        }

        ServiceResult<SubwayStationDTO> subwayStationServiceResult = addressService.findSubwayStation(detailDTO.getSubwayStationId());
        if (subwayStationServiceResult.isSuccess()) {
            model.addAttribute("station", subwayStationServiceResult.getResult());
        }

        return "admin/house-edit";
    }

    /**
     * 编辑接口
     */
    @PostMapping("house/edit")
    @ResponseBody
    public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult result = houseService.update(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }

        ApiResponse response = ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        response.setMessage(result.getMessage());
        return response;
    }

    /**
     * 移除图片接口
     * @param id
     * @return
     */
    @DeleteMapping("house/photo")
    @ResponseBody
    public ApiResponse removeHousePhoto(@RequestParam(value = "id") Long id) {
        ServiceResult result = this.houseService.removePhoto(id);

        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 修改封面接口
     * @param coverId
     * @param targetId
     * @return
     */
    @PostMapping("house/cover")
    @ResponseBody
    public ApiResponse updateCover(@RequestParam(value = "cover_id") Long coverId,
                                   @RequestParam(value = "target_id") Long targetId) {
        ServiceResult result = this.houseService.updateCover(coverId, targetId);

        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 增加标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @PostMapping("house/tag")
    @ResponseBody
    public ApiResponse addHouseTag(@RequestParam(value = "house_id") Long houseId,
                                   @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || StringUtils.isEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.addTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 移除标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @DeleteMapping("house/tag")
    @ResponseBody
    public ApiResponse removeHouseTag(@RequestParam(value = "house_id") Long houseId,
                                      @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || StringUtils.isEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.removeTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 审核接口
     * @param id
     * @param operation
     * @return
     */
    @PutMapping("house/operate/{id}/{operation}")
    @ResponseBody
    public ApiResponse operateHouse(@PathVariable(value = "id") Long id,
                                    @PathVariable(value = "operation") int operation) {
        if (id <= 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult result;

        switch (operation) {
            case HouseOperation.PASS:
                result = this.houseService.updateStatus(id, HouseStatus.PASSES.getValue());
                break;
            case HouseOperation.PULL_OUT:
                result = this.houseService.updateStatus(id, HouseStatus.NOT_AUDITED.getValue());
                break;
            case HouseOperation.DELETE:
                result = this.houseService.updateStatus(id, HouseStatus.DELETED.getValue());
                break;
            case HouseOperation.RENT:
                result = this.houseService.updateStatus(id, HouseStatus.RENTED.getValue());
                break;
            default:
                return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }
        return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),
                result.getMessage());
    }

}
