package xxx.joker.libs.javalibs.repo;

import xxx.joker.libs.javalibs.repository.JkRepoField;
import xxx.joker.libs.javalibs.repository.JkDefaultRepoTable;

public class NewCategory extends JkDefaultRepoTable {

	@JkRepoField(index = 0)
	private String name;

    public NewCategory() {
    }

    public NewCategory(Category cat) {
        name = cat.getName();
    }

    public NewCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String getPrimaryKey() {
        return name != null ? name.toLowerCase() : "";
    }

}
