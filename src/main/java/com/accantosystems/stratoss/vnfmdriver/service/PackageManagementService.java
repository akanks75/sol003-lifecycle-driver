package com.accantosystems.stratoss.vnfmdriver.service;

import org.etsi.sol003.packagemanagement.PackageOnboardingStateType;
import org.etsi.sol003.packagemanagement.PackageOperationalStateType;
import org.etsi.sol003.packagemanagement.VnfPkgInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.accantosystems.stratoss.vnfmdriver.driver.VNFPackageRepositoryDriver;
import com.accantosystems.stratoss.vnfmdriver.web.etsi.ResponseTypeNotAcceptableException;

@Service("PackageManagementService")
public class PackageManagementService {

    private final static Logger logger = LoggerFactory.getLogger(VNFPackageRepositoryDriver.class);

    private final VNFPackageRepositoryDriver vnfPackageDriver;
    private final VNFPackageExtractor vnfPackageExtractor;

    @Autowired
    public PackageManagementService(VNFPackageRepositoryDriver vnfPackageDriver, VNFPackageExtractor vnfPackageExtractor) {
        this.vnfPackageDriver = vnfPackageDriver;
        this.vnfPackageExtractor = vnfPackageExtractor;
    }

    public VnfPkgInfo getVnfPackageInfo(String vnfPkgId) {

        Resource vnfPackageZip = vnfPackageDriver.getVnfPackage(vnfPkgId);
        VnfPkgInfo vnfPkgInfo = vnfPackageExtractor.populateVnfPackageInfo(vnfPkgId, vnfPackageZip);

        vnfPkgInfo.setOnboardingState(PackageOnboardingStateType.ONBOARDED);
        vnfPkgInfo.setOperationalState(PackageOperationalStateType.ENABLED);

        return vnfPkgInfo;
    }

    public String getVnfdAsYaml(String vnfPkgId) throws ResponseTypeNotAcceptableException {

        Resource vnfPackageZip = vnfPackageDriver.getVnfPackage(vnfPkgId);
        String vnfd = vnfPackageExtractor.extractVnfdAsYaml(vnfPkgId, vnfPackageZip);
        return vnfd;

    }

    public Resource getVnfdAsZip(String vnfPkgId) throws ResponseTypeNotAcceptableException {

        Resource vnfPackageZip = vnfPackageDriver.getVnfPackage(vnfPkgId);
        Resource vnfdPackage = vnfPackageExtractor.extractVnfdAsZip(vnfPkgId, vnfPackageZip);
        return vnfdPackage;
    }

    public Resource getVnfPackageContent(String vnfPkgId, String contentRange) throws PackageStateConflictException, ContentRangeNotSatisfiableException {
        Resource vnfPackageZip = vnfPackageDriver.getVnfPackage(vnfPkgId);
        // TODO handle content range
        return vnfPackageZip;
    }

    public Resource getVnfPackageArtifact(String vnfPkgId, String artifactPath, String contentRange) throws PackageStateConflictException, ContentRangeNotSatisfiableException {

        Resource vnfPackageZip = vnfPackageDriver.getVnfPackage(vnfPkgId);
        Resource vnfArtifact = vnfPackageExtractor.extractVnfPackageArtifact(vnfPkgId, artifactPath, vnfPackageZip);
        // TODO handle content range
        return vnfArtifact;
    }

}
