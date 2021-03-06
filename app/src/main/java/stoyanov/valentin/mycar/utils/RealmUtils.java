package stoyanov.valentin.mycar.utils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.Color;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.models.DateNotification;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.ServiceType;
import stoyanov.valentin.mycar.realm.models.Vehicle;

public class RealmUtils {

    public static void deleteProperty(RealmModel model, Constants.ActivityType type) {
        switch (type) {
            case SERVICE:
                Service service = (Service) model;
                DateNotification dateNotification = service.getDateNotification();
                if (dateNotification != null) {
                    dateNotification.deleteFromRealm();
                }
                service.deleteFromRealm();
                break;
            case INSURANCE:
                Insurance insurance = (Insurance) model;
                insurance.getNotification().deleteFromRealm();
                insurance.deleteFromRealm();
                break;
            default:
                ((RealmObject)model).deleteFromRealm();
                break;
        }
    }

    public static void deleteVehicle(Vehicle vehicle) {
        RealmList<Service> services = vehicle.getServices();
        for (int i = services.size() - 1; i >= 0; i--) {
            deleteProperty(services.get(i), Constants.ActivityType.SERVICE);
        }
        services.deleteAllFromRealm();

        RealmList<Insurance> insurances = vehicle.getInsurances();
        for (int i = insurances.size() - 1; i >= 0; i--) {
            deleteProperty(insurances.get(i), Constants.ActivityType.INSURANCE);
        }
        insurances.deleteAllFromRealm();

        RealmList<Refueling> refuelings = vehicle.getRefuelings();
        for (int i = refuelings.size() - 1; i >= 0; i--) {
            deleteProperty(refuelings.get(i), Constants.ActivityType.REFUELING);
        }
        refuelings.deleteAllFromRealm();

        RealmList<Expense> expenses = vehicle.getExpenses();
        for (int i = expenses.size() - 1; i >= 0; i--) {
            deleteProperty(expenses.get(i), Constants.ActivityType.EXPENSE);
        }
        expenses.deleteAllFromRealm();

        vehicle.getFuelTanks().deleteAllFromRealm();
        vehicle.deleteFromRealm();
    }

    public static void importVehicle(final Vehicle vehicle, final boolean exists, Realm myRealm,
                                     Realm.Transaction.OnSuccess onSuccess,
                                     Realm.Transaction.OnError onError) {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (exists) {
                    deleteVehicle(realm.where(Vehicle.class).equalTo(Constants.NAME, vehicle.getName())
                            .findFirst());
                }
                Brand brand = realm.where(Brand.class)
                        .equalTo(Constants.NAME, vehicle.getBrand().getName())
                        .findFirst();
                if (brand == null) {
                    brand = vehicle.getBrand();
                }
                vehicle.setBrand(brand);

                Model model = realm.where(Model.class)
                        .equalTo(Constants.NAME, vehicle.getModel().getName())
                        .findFirst();
                if (model == null) {
                    model = vehicle.getModel();
                }
                vehicle.setModel(model);

                Color color = realm.where(Color.class)
                        .equalTo(Constants.COLOR, vehicle.getColor().getColor())
                        .findFirst();
                if (color == null) {
                    color = vehicle.getColor();
                }
                vehicle.setColor(color);

                for (Insurance insurance : vehicle.getInsurances()) {
                    Company company = realm.where(Company.class)
                            .equalTo(Constants.NAME, insurance.getCompany().getName())
                            .findFirst();
                    if (company == null) {
                        company = insurance.getCompany();
                    }
                    insurance.setCompany(company);
                }

                for (Service service : vehicle.getServices()) {
                    ServiceType serviceType = realm.where(ServiceType.class)
                            .equalTo(Constants.NAME, service.getType().getName())
                            .findFirst();
                    if (serviceType == null) {
                        serviceType = service.getType();
                    }
                    service.setType(serviceType);
                }

                realm.copyToRealmOrUpdate(vehicle);
            }
        }, onSuccess, onError);
    }
}
