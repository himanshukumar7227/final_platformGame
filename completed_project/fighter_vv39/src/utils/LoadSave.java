package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


//import main.Game;

public class LoadSave {
	
	public static final String PLAYER_ATLAS = "player_sprites.png";
	public static final String LEVEL_ATLAS = "outside_sprites.png";
	public static final String MENU_BUTTONS = "button_atlas.png";
	public static final String  MENU_BACKGROUND= "menu_background.png";
	public static final String  PAUSE_BAKGROUND= "pause_menu.png";
	public static final String  SOUND_BUTTONS= "sound_button.png";
	public static final String  URM_BUTTONS= "urm_buttons.png";
	public static final String VOLUME_BUTTONS="volume_buttons.png";
	public static final String MENU_BACKGROUND_IMG="menu_back.png";
	public static final String PLAYING_BG_IMG="playing_bg_img.png";
	public static final String BIG_CLOUDS="big_clouds.png";
	public static final String SMALL_CLOUDS="small_clouds.png";
	public static final String CRABBY_SPRITE="crabby_sprite.png";
	public static final String STATUS_BAR="health_power_bar.png";
	public static final String COMPLETED_IMG="completed_sprite.png";
	public static final String TRAP_ATLAS="trap_atlas.png";
	public static final String CANNON_ATLAS="cannon_atlas.png";
	public static final String CANNON_BALL = "ball.png";
	public static final String DEATH_SCREEN = "death_screen.png";
	public static final String OPTION_BACK = "snow.png";
	public static final String OPTION_BACK_BOX = "options_background.png";
	
	public static final String POITION_ATLAS="potions_sprites.png";
	public static final String CONTAINER_ATLAS="objects_sprites.png";
	public static final String WATER_TOP="water_atlas_animation.png";
	public static final String WATER_BOTTOM="water.png";
	
	
	
	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img=null;
		InputStream is =LoadSave.class.getResourceAsStream("/"+fileName);
		try {
			img = ImageIO.read(is);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				is.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return img;
	}
	
//	public static BufferedImage[] GetAllLevels() {
//		URL url=LoadSave.class.getResource("/lvls");
//		File file =null;
//		
//		try {
//			file=new File(url.toURI());
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//		
//		File[] files = file.listFiles();
//		File[] filesSorted=new File[files.length];
//		
//		for(int i=0;i<filesSorted.length;i++)
//			for(int j=0;j<files.length;j++) {
//				if(files[j].getName().equals((i+1)+".png"))
//					filesSorted[i]=files[j];
//					
//			}
//		
//		BufferedImage[] imgs=new BufferedImage[filesSorted.length];
//		for(int i=0;i<imgs.length;i++)
//			try {
//				imgs[i]=ImageIO.read(filesSorted[i]);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		
//		return imgs;
//		
//	}
	
	public static BufferedImage[] GetAllLevels() {
        List<BufferedImage> images = new ArrayList<>();

        // Load resources from the directory '/lvls'
        String[] fileNames = listFiles();

        // Sort files based on the naming pattern "(i+1).png"
        fileNames = sortFiles(fileNames);

        // Load each file as BufferedImage
        for (String fileName : fileNames) {
            try (InputStream is = LoadSave.class.getResourceAsStream("/lvls/" + fileName)) {
                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    images.add(img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Convert List to array
        BufferedImage[] imgs = new BufferedImage[images.size()];
        return images.toArray(imgs);
    }

    private static String[] listFiles() {
        // In a real implementation, list files in the directory '/lvls'
        // For demonstration, return dummy file names
        return new String[]{"1.png", "2.png", "3.png","4.png","5.png"}; // Adjust based on actual file names
    }

    private static String[] sortFiles(String[] fileNames) {
        // Sort files based on the naming pattern "(i+1).png"
        // This assumes file names are in the format "1.png", "2.png", ..., "n.png"
        // Adjust sorting logic based on your specific naming convention
        List<String> sortedFiles = new ArrayList<>();
        for (int i = 0; i < fileNames.length; i++) {
            for (String fileName : fileNames) {
                if (fileName.equals((i + 1) + ".png")) {
                    sortedFiles.add(fileName);
                    break;
                }
            }
        }
        return sortedFiles.toArray(new String[0]);
    }
	
	
}
