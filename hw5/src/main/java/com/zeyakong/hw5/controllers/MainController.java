package com.zeyakong.hw5.controllers;

import com.zeyakong.hw5.models.User;
import com.zeyakong.hw5.models.Weather;
import com.zeyakong.hw5.services.ImageService;
import com.zeyakong.hw5.services.UserService;
import com.zeyakong.hw5.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addLocationPage(
            @RequestParam(name = "lat", required = false, defaultValue = "")
                    String lat,
            @RequestParam(name = "lon", required = false, defaultValue = "")
                    String lon, Model model,Principal principal
    ) {
        User u= userService.findByUsername(principal.getName());
        model.addAttribute("imgURL",imageService.getAvatarUrl(u));
        Weather w = weatherService.getWeatherObject(lat, lon);
        if (w != null) {
            model.addAttribute("weatherObj", w);
        }
        return "add";
    }

    @RequestMapping(value = "/addLocation", method = RequestMethod.POST)
    public void addLocation(@ModelAttribute Weather weatherObj, HttpServletResponse res) {
        weatherService.saveWeather(weatherObj);
        try {
            res.sendRedirect("/home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String goHome(Model model, Principal principal,
                         @RequestParam(value = "location" ,required = false) String location
    ) {
        // get all weather objects.
        List<Weather> weatherList = weatherService.findAll();

        if (weatherList != null && weatherList.size() > 0) {
            model.addAttribute("list", weatherList);
            if(location!=null&&!location.equals("")){
                model.addAttribute("currentWeather", weatherService.findById(location));
            }else{
                model.addAttribute("currentWeather", weatherList.get(0));
            }
        }
        User u= userService.findByUsername(principal.getName());
        model.addAttribute("imgURL",imageService.getAvatarUrl(u));
        return "home";
    }

    @RequestMapping(value = "/profile",method = RequestMethod.GET)
    public String goProfile (Model model, Principal principal) {
        User u= userService.findByUsername(principal.getName());
        model.addAttribute("user",u);
        model.addAttribute("imgURL",imageService.getAvatarUrl(u));
        return "profile";
    }

    @RequestMapping(value = "/avatars/{uid}", method= RequestMethod.GET)
    public @ResponseBody
    byte[] getAvatarAbsolutePath(
            @PathVariable String uid
    ){
        return imageService.load(uid);
    }

    @RequestMapping(value = "/avatars/{uid}" , method = RequestMethod.POST)
    public void updateAvatar(
            @RequestParam("file") MultipartFile file,
            @PathVariable String uid,
            HttpServletResponse res
    ){
        if(!file.isEmpty()){
            try{
                imageService.save(file,uid);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            res.sendRedirect("/profile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/updateWeather",method = RequestMethod.POST)
    public void updateWeather(
            @RequestParam(value = "weatherId") String id,
            HttpServletResponse resp
    ){
        //update weather object.
        weatherService.updateWeather(weatherService.findById(id));
        try {
            resp.sendRedirect("/home"+"?location=" + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
