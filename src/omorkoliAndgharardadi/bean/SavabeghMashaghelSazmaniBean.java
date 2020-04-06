package omorkoliAndgharardadi.bean;

import amar.model.Personel;
import baseCode.alert.Alert;
import dataBaseModel.authentication.permission.Permission;
import dataBaseModel.authentication.user.User;
import dataBaseModel.authentication.user.UserDao;
import dataBaseModel.baseTable.*;
import dataBaseModel.dao.PersonelDao;
import dataBaseModel.dao.SavabeghMashaghelDao;
import dataBaseModel.util.HibernateUtil;
import manage.bean.IndexBean;
import omorkoliAndgharardadi.model.SavabeghMashaghel;
import org.hibernate.Session;
import util.FillList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class SavabeghMashaghelSazmaniBean implements Serializable {
    private String URL;
    private List<SavabeghMashaghel> savabegh = new ArrayList<>();
    private List<Dayere> dayeres = new ArrayList<>();
    private List<NoeEstekhdam> noeEstekhdams = new ArrayList<>();
    private List<Bakhsh> bakhshes = new ArrayList<>();
    private List<Yegan> series = new ArrayList<>();
    private List<Daraje> darajes = new ArrayList<>();
    private Alert alert = new Alert();

    // search field
    private String shpSearch;
    private String codeMeliSearch;
    private String cartNumberSearch;
    private String nameSearch;
    private String familySearch;
    private String darajeCodeSearch;
    private String yeganCodeSearch;
    private String dayereCodeSearch;
    private Integer vaziyatSearch = 3;

    private String shp;
    private String cm;
    private Personel personel = null;
    private String dayereCode;
    private String bakhshCode;
    private String seriTitle;
    private String darajeCode;
    private String tarikhEntesab;
    private String tarikhEnfesal;
    private String onvaneShoghlSazmani;
    private String onvaneShoghlAmali;
    private String band;
    private String satr;
    private Boolean vaziyat = false;

    private String moshakhasat;

    private SavabeghMashaghel selectMode = null;
    private boolean edited = false;

    private List<Permission> permissions;
    private boolean createPermission = false;
    private boolean readPermission = false;
    private boolean updatePermission = false;
    private boolean deletePermission = false;


    public SavabeghMashaghelSazmaniBean() {
        URL = IndexBean.url;

        permissions = IndexBean.permissions;

        if(permissions.size() > 0){
            createPermission = permissions.get(0).getCreat();
            readPermission = permissions.get(0).getReaad();
            updatePermission = permissions.get(0).getUpdat();
            deletePermission = permissions.get(0).getDelet();
        }

        fillSavabegh();
    }

    private void fillSavabegh(){
        FillList fillList = new FillList();
        dayeres = fillList.dayeres(permissions.get(0));
        noeEstekhdams = fillList.noeEstekhdams(permissions.get(0));

        Session session = HibernateUtil.getSession();
        series = session.createQuery("FROM Yegan").list();
        darajes = session.createQuery("FROM Daraje").list();
        session.close();
    }

    public void fillBakhsh(String code){
        FillList fillList = new FillList();
        bakhshes = fillList.bakhshByDayereCode(permissions.get(0), code);
    }

    public void search(){
        StringBuilder query = new StringBuilder("FROM SavabeghMashaghel WHERE 1 = 1 ");

        if(shpSearch != null && !shpSearch.equals("")){
            query.append("AND personel.shomarePersoneli = '").append(shpSearch).append("' ");
        }

        if(codeMeliSearch != null && !codeMeliSearch.equals("")){
            query.append("AND personel.codeMeli = '").append(codeMeliSearch).append("' ");
        }

        if(cartNumberSearch != null && !cartNumberSearch.equals("")){
            query.append("AND personel.shomareKart = '").append(cartNumberSearch).append("' ");
        }

        if(nameSearch != null && !nameSearch.equals("")){
            query.append("AND personel.name LIKE '%").append(nameSearch).append("%' ");
        }

        if(familySearch != null && !familySearch.equals("")){
            query.append("AND personel.neshan LIKE '%").append(familySearch).append("%' ");
        }

        if(darajeCodeSearch != null && !darajeCodeSearch.equals("")){
            query.append("AND personel.daraje.id = ").append(darajeCodeSearch).append(" ");
        }

        if(yeganCodeSearch != null && !yeganCodeSearch.equals("")){
            query.append("AND personel.yegan.id = ").append(yeganCodeSearch).append(" ");
        }

        if(dayereCodeSearch != null && !dayereCodeSearch.equals("")){
            query.append("AND personel.dayere.id = ").append(dayereCodeSearch).append(" ");
        } else {
            String d = " AND (";
            for (Dayere dayere : dayeres) {
                if(d.equals(" AND (")){
                    d += "personel.dayere.id = " + dayere.getId();
                } else {
                    d += " OR personel.dayere.id = " + dayere.getId();
                }
            }
            if(!d.equals(" AND (")){
                d += ")";
                query.append(d);
            }
        }

        String q = " AND (";
        for (NoeEstekhdam noeEstekhdam : noeEstekhdams) {
            if(q.equals(" AND (")){
                q += "personel.noeEstekhdam.id = " + noeEstekhdam.getId();
            } else {
                q += " OR personel.noeEstekhdam.id = " + noeEstekhdam.getId();
            }
        }
        if(!q.equals(" AND (")){
            q += ")";
            query.append(q);
        }

        FillList fillList = new FillList();
        List<Bakhsh> bakhshFill = fillList.bakhsh(permissions.get(0));
        String b = " AND (";
        for (Bakhsh bakhsh : bakhshFill) {
            if(b.equals(" AND (")){
                b += "personel.bakhsh.id = " + bakhsh.getId();
            } else {
                b += " OR personel.bakhsh.id = " + bakhsh.getId();
            }
        }
        if(!b.equals(" AND (")){
            b += ")";
            query.append(b);
        }

        if(vaziyatSearch == 1){
            query.append("AND vazeyatShagheli = '1' ");
        } else if(vaziyatSearch == 2) {
            query.append("AND vazeyatShagheli = '0' ");
        }

        Session session = HibernateUtil.getSession();
        savabegh = session.createQuery(query.toString()).list();
        session.close();
    }

    public void findPersonel(){
        FillList fillList = new FillList();
        List<Personel> personels = fillList.personels(permissions.get(0), shp, cm, "", "");

        if(personels.size() > 0){
            personel = personels.get(0);
            if(personel.getDaraje() != null) {
                moshakhasat = "نام و نشان: " + personel.getName() + " " + personel.getNeshan() + "     درجه :" + personel.getDaraje().getTitle();
            } else {
                moshakhasat = "نام و نشان: " + personel.getName() + " " + personel.getNeshan();
            }
            shp = personel.getShomarePersoneli();
            cm = personel.getCodeMeli();
        } else {
            personel = null;
            shp = null;
            cm = null;
            moshakhasat = null;
            alert.notFoundPersonel();
        }
    }

    public void save(){
        if(personel != null){
            if(tarikhEntesab != null && !tarikhEntesab.equals("") && dayereCode != null && !dayereCode.equals("") && bakhshCode != null && !bakhshCode.equals("") && seriTitle != null && !seriTitle.equals("") && darajeCode != null && !darajeCode.equals("")){
                SavabeghMashaghel model = new SavabeghMashaghel();

                model.setPersonel(personel);
                model.setDayere(dayeres.stream().filter(o -> o.getCode().equals(dayereCode)).findFirst().orElse(null));
                model.setBakhsh(bakhshes.stream().filter(o -> o.getCode().equals(bakhshCode)).findFirst().orElse(null));
                model.setSeri(seriTitle);
                model.setDaraje(darajes.stream().filter(o -> o.getCode().equals(darajeCode)).findFirst().orElse(null));
                model.setTarikhEntesab(tarikhEntesab);
                model.setTarikhEnfesal(tarikhEnfesal);
                model.setTitleShoghlSazmani(onvaneShoghlSazmani);
                model.setTitleShoghlAmali(onvaneShoghlAmali);
                model.setBand(band);
                model.setSatr(satr);
                model.setVazeyatShagheli(vaziyat);

                SavabeghMashaghelDao.getInstance().getBaseQuery().create(model, URL);

                alert.successSave();

                savabegh.add(model);

                personel.setDarajeShoghl(darajes.stream().filter(o -> o.getCode().equals(darajeCode)).findFirst().orElse(null));
                personel.setSeri(seriTitle);
                personel.setBand(band);
                personel.setSatr(satr);
                personel.setOnvaneShoghl(onvaneShoghlSazmani);
                personel.setMahalShoghlSazmani(onvaneShoghlAmali);
                personel.setDakhelOrKharej(vaziyat);
                PersonelDao.getInstance().getBaseQuery().createOrUpdate(personel, URL);

                nuller();
            } else {
                alert.fieldIsEmpty("تمامی فیلدها");
            }
        } else {
            alert.fieldIsEmpty("شماره پرسنلی");
        }
    }

    public void nuller() {
        shp = "";
        personel = null;

        dayereCode = "";
        bakhshCode = "";
        seriTitle = "";
        tarikhEntesab = "";
        tarikhEnfesal = "";
        onvaneShoghlSazmani = "";
        onvaneShoghlAmali = "";
        band = "";
        satr = "";
        darajeCode = "";
        vaziyat = false;

        moshakhasat = null;

        edited = false;
        selectMode = null;
    }

    public void dispach(SavabeghMashaghel model){
        edited = true;
        shp = model.getPersonel().getShomarePersoneli();
        personel = model.getPersonel();
        if(personel.getDaraje() != null) {
            moshakhasat = "نام و نشان: " + personel.getName() + " " + personel.getNeshan() + "     درجه :" + personel.getDaraje().getTitle();
        } else {
            moshakhasat = "نام و نشان: " + personel.getName() + " " + personel.getNeshan();
        }

        dayereCode = model.getDayere().getCode();
        bakhshCode = model.getBakhsh().getCode();
        if(model.getDaraje() != null) {
            darajeCode = model.getDaraje().getCode();
        }
        seriTitle = model.getSeri();
        tarikhEntesab = model.getTarikhEntesab();
        tarikhEnfesal = model.getTarikhEnfesal();
        onvaneShoghlSazmani = model.getTitleShoghlSazmani();
        onvaneShoghlAmali = model.getTitleShoghlAmali();
        band = model.getBand();
        satr = model.getSatr();
        vaziyat = model.isVazeyatShagheli();

        selectMode = model;
    }

    public void edit(){
        if(personel != null && selectMode != null){
            if(tarikhEntesab != null && !tarikhEntesab.equals("") && dayereCode != null && !dayereCode.equals("") && bakhshCode != null && !bakhshCode.equals("") && seriTitle != null && !seriTitle.equals("") && darajeCode != null && !darajeCode.equals("")){
                SavabeghMashaghel model = selectMode;

                model.setPersonel(personel);
                model.setDayere(dayeres.stream().filter(o -> o.getCode().equals(dayereCode)).findFirst().orElse(null));
                model.setBakhsh(bakhshes.stream().filter(o -> o.getCode().equals(bakhshCode)).findFirst().orElse(null));
                model.setSeri(seriTitle);
                model.setDaraje(darajes.stream().filter(o -> o.getCode().equals(darajeCode)).findFirst().orElse(null));
                model.setTarikhEntesab(tarikhEntesab);
                model.setTarikhEnfesal(tarikhEnfesal);
                model.setTitleShoghlSazmani(onvaneShoghlSazmani);
                model.setTitleShoghlAmali(onvaneShoghlAmali);
                model.setBand(band);
                model.setSatr(satr);
                model.setVazeyatShagheli(vaziyat);

                SavabeghMashaghelDao.getInstance().getBaseQuery().createOrUpdate(model, URL);

                alert.successEdit();

                savabegh.remove(selectMode);
                savabegh.add(model);

                personel.setDarajeShoghl(darajes.stream().filter(o -> o.getCode().equals(darajeCode)).findFirst().orElse(null));
                personel.setSeri(seriTitle);
                personel.setBand(band);
                personel.setSatr(satr);
                personel.setOnvaneShoghl(onvaneShoghlSazmani);
                personel.setMahalShoghlSazmani(onvaneShoghlAmali);
                personel.setDakhelOrKharej(vaziyat);
                PersonelDao.getInstance().getBaseQuery().createOrUpdate(personel, URL);

                nuller();
            } else {
                alert.fieldIsEmpty("تمامی فیلدها");
            }
        } else {
            alert.fieldIsEmpty("شماره پرسنلی");
        }
    }

    public void startDelete(SavabeghMashaghel model){
        selectMode = model;
    }

    public void cancelDelete(){
        selectMode = null;
    }

    public void delete(){
        SavabeghMashaghelDao.getInstance().getBaseQuery().delete(selectMode);
        savabegh.remove(selectMode);
        alert.successDelete();
        selectMode = null;
        nuller();
    }

    public String checkVaziyat(Boolean taed){
        if(taed) return "داخل کارخانجات";
        else return "خارج کارخانجات";
    }




    // GETTER AND SETTER

    public List<SavabeghMashaghel> getSavabegh() {
        return savabegh;
    }

    public void setSavabegh(List<SavabeghMashaghel> savabegh) {
        this.savabegh = savabegh;
    }

    public List<Dayere> getDayeres() {
        return dayeres;
    }

    public void setDayeres(List<Dayere> dayeres) {
        this.dayeres = dayeres;
    }

    public List<Bakhsh> getBakhshes() {
        return bakhshes;
    }

    public void setBakhshes(List<Bakhsh> bakhshes) {
        this.bakhshes = bakhshes;
    }

    public List<Yegan> getSeries() {
        return series;
    }

    public void setSeries(List<Yegan> series) {
        this.series = series;
    }

    public List<Daraje> getDarajes() {
        return darajes;
    }

    public void setDarajes(List<Daraje> darajes) {
        this.darajes = darajes;
    }

    public String getShpSearch() {
        return shpSearch;
    }

    public void setShpSearch(String shpSearch) {
        this.shpSearch = shpSearch;
    }

    public String getCodeMeliSearch() {
        return codeMeliSearch;
    }

    public void setCodeMeliSearch(String codeMeliSearch) {
        this.codeMeliSearch = codeMeliSearch;
    }

    public String getCartNumberSearch() {
        return cartNumberSearch;
    }

    public void setCartNumberSearch(String cartNumberSearch) {
        this.cartNumberSearch = cartNumberSearch;
    }

    public String getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(String nameSearch) {
        this.nameSearch = nameSearch;
    }

    public String getFamilySearch() {
        return familySearch;
    }

    public void setFamilySearch(String familySearch) {
        this.familySearch = familySearch;
    }

    public String getDarajeCodeSearch() {
        return darajeCodeSearch;
    }

    public void setDarajeCodeSearch(String darajeCodeSearch) {
        this.darajeCodeSearch = darajeCodeSearch;
    }

    public String getYeganCodeSearch() {
        return yeganCodeSearch;
    }

    public void setYeganCodeSearch(String yeganCodeSearch) {
        this.yeganCodeSearch = yeganCodeSearch;
    }

    public String getDayereCodeSearch() {
        return dayereCodeSearch;
    }

    public void setDayereCodeSearch(String dayereCodeSearch) {
        this.dayereCodeSearch = dayereCodeSearch;
    }

    public Integer getVaziyatSearch() {
        return vaziyatSearch;
    }

    public void setVaziyatSearch(Integer vaziyatSearch) {
        this.vaziyatSearch = vaziyatSearch;
    }

    public String getShp() {
        return shp;
    }

    public void setShp(String shp) {
        this.shp = shp;
    }

    public Personel getPersonel() {
        return personel;
    }

    public void setPersonel(Personel personel) {
        this.personel = personel;
    }

    public String getDayereCode() {
        return dayereCode;
    }

    public void setDayereCode(String dayereCode) {
        this.dayereCode = dayereCode;
    }

    public String getBakhshCode() {
        return bakhshCode;
    }

    public void setBakhshCode(String bakhshCode) {
        this.bakhshCode = bakhshCode;
    }

    public String getSeriTitle() {
        return seriTitle;
    }

    public void setSeriTitle(String seriTitle) {
        this.seriTitle = seriTitle;
    }

    public String getTarikhEntesab() {
        return tarikhEntesab;
    }

    public void setTarikhEntesab(String tarikhEntesab) {
        this.tarikhEntesab = tarikhEntesab;
    }

    public String getTarikhEnfesal() {
        return tarikhEnfesal;
    }

    public void setTarikhEnfesal(String tarikhEnfesal) {
        this.tarikhEnfesal = tarikhEnfesal;
    }

    public String getOnvaneShoghlSazmani() {
        return onvaneShoghlSazmani;
    }

    public void setOnvaneShoghlSazmani(String onvaneShoghlSazmani) {
        this.onvaneShoghlSazmani = onvaneShoghlSazmani;
    }

    public String getOnvaneShoghlAmali() {
        return onvaneShoghlAmali;
    }

    public void setOnvaneShoghlAmali(String onvaneShoghlAmali) {
        this.onvaneShoghlAmali = onvaneShoghlAmali;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getSatr() {
        return satr;
    }

    public void setSatr(String satr) {
        this.satr = satr;
    }

    public Boolean getVaziyat() {
        return vaziyat;
    }

    public void setVaziyat(Boolean vaziyat) {
        this.vaziyat = vaziyat;
    }

    public String getMoshakhasat() {
        return moshakhasat;
    }

    public void setMoshakhasat(String moshakhasat) {
        this.moshakhasat = moshakhasat;
    }

    public SavabeghMashaghel getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(SavabeghMashaghel selectMode) {
        this.selectMode = selectMode;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public String getCm() {
        return cm;
    }

    public void setCm(String cm) {
        this.cm = cm;
    }

    public boolean isCreatePermission() {
        return createPermission;
    }

    public void setCreatePermission(boolean createPermission) {
        this.createPermission = createPermission;
    }

    public boolean isReadPermission() {
        return readPermission;
    }

    public void setReadPermission(boolean readPermission) {
        this.readPermission = readPermission;
    }

    public boolean isUpdatePermission() {
        return updatePermission;
    }

    public void setUpdatePermission(boolean updatePermission) {
        this.updatePermission = updatePermission;
    }

    public boolean isDeletePermission() {
        return deletePermission;
    }

    public void setDeletePermission(boolean deletePermission) {
        this.deletePermission = deletePermission;
    }

    public String getDarajeCode() {
        return darajeCode;
    }

    public void setDarajeCode(String darajeCode) {
        this.darajeCode = darajeCode;
    }
}