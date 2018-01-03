package sorcer.service;

import sorcer.core.Tag;
import sorcer.service.modeling.Data;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mike Sobolewski on 6/27/16.
 */
public class Fidelity<T> implements Fi<T>, Dependency, net.jini.core.entry.Entry, Activity {
    static final long serialVersionUID = 1L;

	protected static int count = 0;

	protected String fiName;

	protected String path = "";

	protected Object option = "";

	protected T select;

    protected boolean changed;

	protected List<T> selects = new ArrayList<T>();

	public Type fiType = Type.SELECT;

	// dependency management for this Fidelity
	protected List<Evaluation> dependers = new ArrayList<Evaluation>();

	public Fidelity() {
		super();
		fiName = "fidelity" + count++;
	}

	public Fidelity(String name) {
		this();
		this.fiName = name;
	}

	public Fidelity(String name, String path) {
		this();
		this.fiName = name;
		this.path = path;
	}

    public Fidelity(String name, String path, String select) {
        this();
        this.fiName = name;
        this.path = path;
        this.select = (T) select;
    }

    public Fidelity(String... names) {
        this.fiName = "";
        fiType = Type.NAME;
        for (String s : names) {
            this.selects.add((T) new Tag(s));
        }
    }

    public Fidelity(String name, List<Fi> fis) {
        this.fiName = name;
        fiType = Type.NAME;
        for (Fi fi : fis)
            this.selects.add((T) fi);
    }

    public Fidelity(Data... entries) {
        fiType = Type.NAME;
        for (Data fi : entries)
            this.selects.add((T) fi);
    }

    @Override
    public Object getId() {
        return fiName;
    }

    @Override
	public String getName() {
		return fiName;
	}

	public void setName(String name) {
		this.fiName = name;
	}

    public T get(int index) {
        return selects.get(index);
    }

    public T getSelect() {
		// if a select not set return the firts one option
		if (select == null && selects.size() > 0) {
			select = selects.get(0);
		}
		return select;
	}

    public String getSelectName() {
        return ((Identifiable)select).getName();
    }

    public List<T> getSelects() {
		return selects;
	}

    public void addSelect(T select) {
        selects.add(select);
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSelect(T select) {
		this.select = select;
	}

	public T selectSelect(String fiName) {
		for (T item : selects) {
			if (((Identifiable)item).getName().equals(fiName)) {
				this.select = item;
				break;
			}
		}
		return (T) select;
	}

	public T getSelect(String name) {
		for (T s : selects) {
			if (((Identifiable)s).getName().equals(name)) {
				return s;
			}
		}
		return null;
	}

	public void setSelects(List<T> selects) {
		this.selects = selects;
	}

    public List<String> getSelectNames() {
        List<String> names = new ArrayList<>(selects.size());
        for (T item : selects) {
            names.add(((Identifiable)item).getName());
        }
        return names;
    }

    public Type getFiType() {
		return fiType;
	}

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean state) {
        changed = state;
    }

    public void setType(Type fiType) {
		this.fiType = fiType;
	}

	@Override
	public int hashCode() {
		String id = fiName + path + select;
		int hash = id.length() + 1;
		return hash * 31 + id.hashCode();
	}

	@Override
	public boolean equals(Object object) {

		if(object == this) {
			return true;
		}

		if (object instanceof Fidelity) {
			Boolean selectorEquality = true;
			if  (((Fidelity)object).getSelect() != null && select != null) {
				selectorEquality = ((Fidelity) object).getSelect().equals(select);
			}

			if (((Fidelity) object).getName().equals(fiName)
					&& ((Fidelity) object).getPath().equals(path)
					&& selectorEquality
					&& ((Fidelity) object).getFiType().equals(fiType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isValid() {
		//reimplement in subclasses
		return true;
	}

    @Override
	public String toString() {
		return (path == null ? fiName :
				fiName + "@" + path)
                + (select != null ? ":" + select : "")
                + (fiType != null ? ":" + fiType : "");
	}

    @Override
    public Object execute(Arg... args) throws ServiceException, RemoteException {
        if (select instanceof Activity) {
            return ((Activity)select).execute(args);
        } else {
            return select;
        }
    }

	@Override
	public void addDependers(Evaluation... dependers) {
		if (this.dependers == null)
			this.dependers = new ArrayList<Evaluation>();
		for (Evaluation depender : dependers)
			this.dependers.add(depender);
	}

	public Object getOption() {
		return option;
	}

	public void setOption(Object option) {
		this.option = option;
	}

	@Override
	public List<Evaluation> getDependers() {
		return dependers;
	}

	@Override
	public Data act(Arg... args) throws ServiceException, RemoteException {
		return new Association(((Identifiable)select).getName(), ((Service)select).execute(args));
	}

	@Override
	public Data act(String entryName, Arg... args) throws ServiceException, RemoteException {
		return new Association(entryName, ((Service)select).execute(args));
	}
}
