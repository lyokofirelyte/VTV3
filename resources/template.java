import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class %replace% {
	
	private VTAction vta = new VTAction();
	
	public static void main(String[] args){
		System.out.println("Initalized VTV3 Compiler Class!");
	}
	
	public String parseItem(String message, Object... objs){

		for (Object o : objs){
			if (o instanceof Player){
				Player player = (Player) o;
				message = r(message,
					"<playername>", player.getName(),
					"<playeruuid>", player.getUniqueId().toString(),
					"<playerdisplayname>", player.getDisplayName()
				);
			} else if (o instanceof Block){
				Block b = (Block) o;
				message = r(message,
					"<blocktype>", b.getType().name()
				);
			}
		}
		
		return message;
	}
	
	private String r(String orig, String... a){
		for (int i = 0; i < a.length; i += 2){
			orig = orig.replace(a[i], a[i+1]);
		}
		return orig;
	}
	
	class VTAction {

		// @PLAYER
		public void PLAYER(Player SENDER, String MESSAGE){
			SENDER.sendMessage(ChatColor.translateAlternateColorCodes('&', MESSAGE));
		}
		
		// @SETBLOCK <type:id> <location>
		public void SETBLOCK(String MESSAGE){
			String[] args = MESSAGE.split(" ");
			
		}
	}